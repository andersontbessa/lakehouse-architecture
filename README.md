# lakehouse-architecture

Concluí um projeto utilizando-se de uma arquitetura de lakehouse tendo em vista os serviços de nuvem da Microsoft Azure.



O intuito do projeto é aprofundar os conhecimentos em engenharia de dados na plataforma Microsoft Azure, e aproveitei minha experiência em Data Sciente para fazer um sistema de recomendação vinculada a arquitetura.



Aqui vai um resumo do que fiz:



1. Criei um grupo de recurso para vincular os serviços que irei implementar.



2. Criei um servidor e um SQL Database da Azure para armazenar dados de testes.



3. No Storage Account, montei um Data Lake com de três camadas, cada qual em seu conteiner, sendo divididos no modelo bronze, silver e gold.



4. Criei pipeline no Data Factory para extrair os dados que estão no SQL Database Azure e armazenar no Data Lake.



5. Criei um Cluster no Azure Databricks para utilizar os recursos do mesmo. Por meio das montagens(mount) no Databricks acessei os dados que estão no Data Lake.



6. Depois de todo sistema estar interagindo, ainda no Azure Databricks, criei um notebook para desenvolver um sistema de recomendação com Machine learning e Apache Spark.



7. O sistema de recomendação tem como princípio fazer a filtragem colaborativa das avaliações dadas por usuários aos filmes, utilizando-se os dataset públicos fornecidos pelo MovieLens.



8. Fiz uma análise dos dataset e os tratei. Em relação ao algoritmo de Machine Learning, optei por utilizar o algoritmo ALS (Alternative Least Squares), implementado pelo Apache Spark ML.

Os dados foram separados em treino e teste por meio do Hold Out (75% e 25%), treinar o modelo e fazer as predições. Logo após, foi feito o cálculo do RMSE entre os valores reais e predições de hipóteses para analisar a performance do algoritmo.

Ao final, foram listados os top 10 filmes recomendados para cada usuário e de usuário recomendados para cada filme.



9. Por fim, é feita a ingestão de arquivos(.Parquet) das camadas do Data Lake, por meio do Delta Lake.
