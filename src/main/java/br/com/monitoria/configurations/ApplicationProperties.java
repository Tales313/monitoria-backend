package br.com.monitoria.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private final ApiInfo apiInfo = new ApiInfo();

    public static class ApiInfo {
        private String titulo;
        private String descricao;
        private String versao;
        public String getTitulo() {
            return titulo;
        }
        public String getDescricao() {
            return descricao;
        }
        public String getVersao() {
            return versao;
        }
        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }
        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
        public void setVersao(String versao) {
            this.versao = versao;
        }
    }

    public ApiInfo getApiInfo() {
        return apiInfo;
    }
}
