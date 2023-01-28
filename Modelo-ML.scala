// Databricks notebook source
val ratings_DF = (spark.read
                  .option("header", "true")
                  .option("inferSchema", "true")
                  .csv("/mnt/stglakehouse/movie/ratings.csv")
                  .select("userId", "movieId", "rating")
                  ).cache()

// COMMAND ----------

val movies_DF = (spark.read
                 .option("header", "true")
                 .option("inferSchema", "true")
                 .csv("/mnt/stglakehouse/movie/movies.csv")
                 ).cache()

// COMMAND ----------

val tags_DF = (spark.read
               .option("header", "true")
               .option("inferSchema", "true")
               .csv("/mnt/stglakehouse/movie/tags.csv")
               .select("userId", "movieId", "tag")
               ).cache()

// COMMAND ----------

ratings_DF.show(10)


// COMMAND ----------

//Faz um left join tendo em vista a chave comum entre rating e o resto.
val ratings2_DF = (ratings_DF
                   .hint("broadcast")
                   .join(movies_DF, Seq("movieId"), "left")
                   .join(tags_DF, Seq("userId", "movieId"), "left")
                   .orderBy("userId", "movieId")
                  )

// COMMAND ----------

ratings2_DF.show(10)

// COMMAND ----------

// DBTITLE 1,Split na coluna genres
//split na coluna genres
import org.apache.spark.sql.functions.{split, explode, col, count, desc, asc}

import org.apache.spark.sql.functions._

val ratings3_DF = (ratings2_DF
                   .withColumn("title", trim(col("title")))
                   .withColumn("genres2", explode(split(col("genres"), "[|]")))
                   )


// COMMAND ----------

// DBTITLE 1,Machine Learning - Filtragem Colaborativa
import org.apache.spark.sql.functions.{split, explode, col, count, desc, asc}

val train_size = 0.75
val test_size = 1 - train_size

val Array(train, test) = ratings_DF.randomSplit(Array(train_size, test_size))


// COMMAND ----------

import org.apache.spark.ml.recommendation.ALS

val als = (new ALS()
           .setMaxIter(15)
           .setAlpha(1.00)
           .setSeed(20111974)
           .setImplicitPrefs(false)
           .setRegParam(0.01)
           .setUserCol("userId")//identifica o usuário
           .setNonnegative(true)
           .setItemCol("movieId")//identifica os filmes
           .setRatingCol("rating")//onde estão as notas de avaliação
           )

// COMMAND ----------

val model = als.fit(train) //passa a amostra de treinamento pro algoritmo de ML.


// COMMAND ----------

// DBTITLE 1,Predição com base no meu modelo treinado e minha base de teste original
model.setColdStartStrategy("drop")
val predictions_test_DF = (model
                           .transform(test)
                           .withColumn("error", round(abs(col("rating") - col("prediction")), 1))//ajustar as casas decimais
                           .withColumn("prediction", round(col("prediction"), 1)) //ajustar as casas decimais
                           .orderBy("movieId", "rating")
                           ).na.drop()
                           
display(predictions_test_DF)

// COMMAND ----------

// DBTITLE 1,Predição com base no meu modelo treinado e minha base de treino original
val predictions_train_DF = (model
                            .transform(train)
                            .withColumn("error", round(abs(col("rating") - col("prediction")), 1))
                            .withColumn("prediction", round(col("prediction"), 1))
                            .orderBy("movieId", "rating")
                           ).na.drop()
display(predictions_train_DF)

// COMMAND ----------

// DBTITLE 1,Calcular o RMSE para o resultado da validação de testes
//é a medida que calcula "a raiz quadrática média" dos erros entre valores observados (reais) e predições (hipóteses).
//
import org.apache.spark.ml.evaluation.RegressionEvaluator

val evaluator = (new RegressionEvaluator()
                 .setMetricName("rmse")
                 .setLabelCol("rating")
                 .setPredictionCol("prediction")
                 )

val rmse = evaluator.evaluate(predictions_test_DF)
println(s"Root-mean-square error = $rmse")


// COMMAND ----------

// DBTITLE 1,Top 10 filmes recomendados para cada usuário
val userRecs_DF = model.recommendForAllUsers(10).orderBy("userId")

display(userRecs_DF)

// COMMAND ----------

// DBTITLE 1,Top 10 usuários para cada filme
val movieRecs_DF = model.recommendForAllItems(10).orderBy("movieId")

display(movieRecs_DF.limit(50))
