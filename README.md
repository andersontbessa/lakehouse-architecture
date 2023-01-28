# lakehouse-architecture

Concluí um projeto com teor de apredizagem utilizando-se de uma arquitetura de lakehouse tendo em vista os serviços de nuvem da Microsoft Azure.
O intuito do projeto é aprofundar os conhecimentos em engenharia de dados na plataforma da Azure, e aproveitei minha experiência em Data Sciente para fazer um sistema de recomendação vinculada a arquitetura.

Aqui vai um resumo do que fiz:

1. Criei um grupo de recurso para vincular os serviços que irei implementar.

2. Criei um servidor e um SQL database da Azure para armazenar dados de testes.

3. No Storage Account, montei um data lake com de três camadas, cada qual em seu conteiner, sendo divididos no modelo bronze, silver e gold.

4. Criei pipelines no Data Factory para extrair os dados que estão no SQL database azure e armazenar no data lake.

5. Criei um cluster no Azure Databricks para utilizar os recursos do mesmo. Por meio das montagens(mount) acessei os dados do data lake no Azure Databricks.

6. Depois de todo sistema estar interagindo, ainda no Azure Databricks, criei um notebook para desenvolver um  sistema de recomendação com Machine learning e Apache Spark.

7. O sistema de recomendação tem como objetivo fazer filtragem colaborativa das classificações de filmes, utilizando os dataset públicos fornecidos pelo MovieLens.

8. Depois de buscar os dados do data lake para o notebook, fiz uma análise dos dataset e os tratei. Em relação ao algoritmo de Machine Learning, optei por utilizar o algoritmo ALS (Alternative Least Squares), implementado pelo Apache Spark ML.
Os dados foram separados em treino e teste por meio do Hold Out (75% e 25%), treinar o modelo e fazer as predições. Logo após, calculei o RMSE entre os valores reais e predições de hipóteses para analisar a performance do algoritmo.
Ao final, foram listados os top 10 filmes recomendados para cada usuário e de usuário recomendados para cada filme.

9. Por fim, é feita a ingestão de arquivos parquet, das camadas do data lake, por meio do delta lake.
