FROM mysql:5.7 AS mysql

# Configurações do MySQL
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=todosimple
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=root

FROM maven:3.8.3-openjdk-17 AS builder

ENV PROJECT_HOME /usr/src/todosimpleapp
ENV JAR_NAME todosimpleapp.jar

# Criar diretório do projeto
RUN mkdir -p $PROJECT_HOME
WORKDIR $PROJECT_HOME

# Copiar o código fonte
COPY . .

# Empacotar a aplicação como JAR
RUN mvn clean package -DskipTests

# Etapa 3: Criar imagem final com MySQL e app Java
FROM openjdk:17-jdk AS final

# Configurações do MySQL
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=todosimple
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=root

# Criar diretório para a aplicação
WORKDIR /usr/src/todosimpleapp

# Copiar o MySQL e o JAR gerado
COPY --from=mysql /var/lib/mysql /var/lib/mysql
COPY --from=builder /usr/src/todosimpleapp/target/$JAR_NAME /usr/src/todosimpleapp/todosimpleapp.jar

# Expor portas
EXPOSE 8080 3306

# Script de inicialização para garantir que o MySQL inicie antes da aplicação
COPY wait-for-mysql.sh /usr/src/todosimpleapp/wait-for-mysql.sh
RUN chmod +x /usr/src/todosimpleapp/wait-for-mysql.sh

ENTRYPOINT ["bash", "-c", "/usr/src/todosimpleapp/wait-for-mysql.sh && java -jar -Dspring.profiles.active=prod todosimpleapp.jar"]