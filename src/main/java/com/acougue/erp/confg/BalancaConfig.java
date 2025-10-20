package com.acougue.erp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.balanca")
public class BalancaConfig {

    private Filizola filizola;
    private Toledo toledo;

    public static class Filizola {
        private String porta;
        private int baudRate;

        public String getPorta() { return porta; }
        public void setPorta(String porta) { this.porta = porta; }
        public int getBaudRate() { return baudRate; }
        public void setBaudRate(int baudRate) { this.baudRate = baudRate; }
    }

    public static class Toledo {
        private String porta;
        private int baudRate;

        public String getPorta() { return porta; }
        public void setPorta(String porta) { this.porta = porta; }
        public int getBaudRate() { return baudRate; }
        public void setBaudRate(int baudRate) { this.baudRate = baudRate; }
    }

    public Filizola getFilizola() { return filizola; }
    public void setFilizola(Filizola filizola) { this.filizola = filizola; }
    public Toledo getToledo() { return toledo; }
    public void setToledo(Toledo toledo) { this.toledo = toledo; }
}