# Prometheus + Grafana (Spring Boot)

Projeto de exemplo em Spring Boot (Java 25) para demonstrar monitoramento com Prometheus e Grafana.
O objetivo é fornecer endpoints REST simples e endpoints de simulação de carga (latência, erros, consumo de memória) para uso em vídeos/tutoriais.


Conteúdo:
- [Como rodar a aplicação localmente](#rodando-localmente)
- [Como rodar Prometheus e Grafana com Docker Compose](#docker-compose)
- [Como configurar o Prometheus para acessar o endpoint protegido do Actuator (/actuator/prometheus)](#configurar-prometheus)
- [Como importar o dashboard do Grafana (`dashboard-grafana.json`)](#importar-dashboard)
- [Testes rápidos com curl (Prometheus / Actuator / Grafana)](#testes-rapidos-com-curl)

---

### Requisitos
- Java 25 (ou JDK compatível)
- Maven 3.8+
- Docker & Docker Compose (apenas se for usar o stack Prometheus/Grafana)

### Estrutura relevante
- `/src/main/java` - código Spring Boot (controllers, service, domain, config)
  - `ProductController` - endpoints CRUD simples (`/api/products`)
  - `SimulationController` - endpoints de simulação: latência, instabilidade, consumo de memória
  - `SecurityConfig` - proteção com HTTP Basic para `/actuator/**` (usuário default: `prometheus` / `prometheus`)
- `application.properties` - configurações H2, JPA, Actuator, logging e credenciais do actuator
- `prometheus.yml` - configuração do Prometheus (exemplo)
- `docker-compose.yml` - define containers para Prometheus e Grafana

Credenciais padrão do Actuator (usadas pelo Prometheus neste projeto)
- username: prometheus
- password: prometheus

Você pode alterar essas credenciais em `src/main/resources/application.properties` (propriedades `management.security.username` e `management.security.password`).

---

<a id="rodando-localmente"></a>
### Rodando localmente (aplicação Spring Boot)

1) Build via Maven (Windows - cmd.exe):

```cmd
cd prometheus-grafana
mvn -DskipTests package
```

2) Rodar com Maven (modo desenvolvimento):

```cmd
mvn spring-boot:run
```

Ou executar o jar gerado:

```cmd
java -jar target\prometheus-grafana-0.0.1-SNAPSHOT.jar
```

Aviso: crie a pasta `logs` no diretório do projeto antes de iniciar, para evitar problemas com o endpoint `logfile`:

```cmd
mkdir logs
```

---

<a id="docker-compose"></a>
### Rodando Prometheus e Grafana com Docker Compose

O arquivo `docker-compose.yml` já inclui serviços para Prometheus e Grafana. Para subir o stack:

```cmd
cd prometheus-grafana
docker-compose up -d
```

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (usuário e senha padrão do Grafana: admin/admin)

Observação importante (Windows/docker): o `prometheus.yml` do repositório usa `host.docker.internal:8080` como target para a aplicação que roda na sua máquina host. Isso permite que o container do Prometheus acesse a aplicação Spring Boot em execução no host Windows.

---

<a id="configurar-prometheus"></a>
### Configurar o Prometheus para acessar o Actuator protegido (basic_auth)

O Actuator foi protegido com HTTP Basic. Você deve configurar o `prometheus.yml` para fornecer credenciais quando o Prometheus fizer o scrape.

Exemplo (adicione `basic_auth` ao `scrape_config`):

```yaml
global:
  scrape_interval: 5s
scrape_configs:
  - job_name: 'app_metrics'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
        labels:
          application: 'prometheus-grafana'
    basic_auth:
      username: 'prometheus'
      password: 'prometheus'
```

Se você estiver usando o `docker-compose.yml` existente, edite o `prometheus.yml` no host (no diretório do projeto) e reinicie o container do Prometheus:

```cmd
# após editar prometheus.yml
docker-compose restart prometheus
```

---

<a id="importar-dashboard"></a>
### Importando o dashboard do Grafana

O repositório inclui um arquivo chamado `dashboard-grafana.json` que contém um dashboard de exemplo pronto para importar no Grafana. Você pode importar usando a interface do Grafana ou pela API.

- Abra o Grafana em http://localhost:3000 e faça login (por padrão: admin/admin).
- No menu lateral, clique em + -> Import.
- Você pode arrastar/soltar o arquivo `dashboard-grafana.json` ou abrir o arquivo, copiar o JSON e colar na área "Import via panel json".
- Escolha a pasta (ou Home) e clique em Import.

---

<a id="testes-rapidos-com-curl"></a>
### Testes rápidos com curl (Prometheus / Actuator / Grafana)

Abaixo há exemplos simples de como testar se cada peça do stack está funcionando. Os comandos foram escritos para `cmd.exe` (Windows). Se você estiver em Linux/macOS, remova pequenas diferenças (por exemplo `NUL` -> `/dev/null`) e use `for` do shell apropriado.

1) Verificar se o Prometheus tem targets registrados

```cmd
curl http://localhost:9090/api/v1/targets
```

Você deve ver o JSON com os targets e o estado de scraping.

2) Acessar o Actuator protegido (exemplo: `/actuator/health` e `/actuator/prometheus`)

- Teste de health (com credenciais HTTP Basic):

```cmd
curl -u prometheus:prometheus http://localhost:8080/actuator/health
```

- Verificar as métricas do Actuator (usado pelo Prometheus):

```cmd
curl -u prometheus:prometheus http://localhost:8080/actuator/prometheus
```

Se você estiver testando a partir de dentro de containers (ou for necessário usar o endereço que o Prometheus usa no Compose), troque `localhost` por `host.docker.internal`:

```cmd
curl -u prometheus:prometheus http://host.docker.internal:8080/actuator/prometheus
```

3) Testar os endpoints de simulação (latência / instabilidade / memória)

- Simular instabilidade (50% de erros):

```cmd
curl -v http://localhost:8080/api/simulations/unstable
```

- Simular latência (cada requisição espera ~2s):

```cmd
curl -v http://localhost:8080/api/simulations/slow
```

- Simular pico de memória (um único disparo):

```cmd
curl -v http://localhost:8080/api/simulations/consumeMemory
```

Interessante gerar uma carga contínua (ex.: via script ou ferramenta como Apache JMeter) para observar as métricas no Prometheus/Grafana.
