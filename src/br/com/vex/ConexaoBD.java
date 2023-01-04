package br.com.vex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    // Declarar a conexão como um campo de classe
    private static final String URL = "jdbc:mysql://localhost:3306/testevex";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin";

    // Conexão = null
    private static ConexaoBD instance = null;
    private Connection connection = null;

    // Construtor privado para impedir que a classe seja instanciada
    private ConexaoBD() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Falha ao estabelecer conexão: " + e.getMessage());
        }
    }

    // Método público e estático para retornar a única instância da classe
    public static ConexaoBD getInstance() {
        if (instance == null) {
            instance = new ConexaoBD();
        }
        return instance;
    }

    // Método público para retornar a conexão
    public Connection getConnection() {
        return connection;
    }

	public void finalize() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Conexão fechada com sucesso!");
			} catch (SQLException e) {
				System.out.println("Falha ao fechar conexão: " + e.getMessage());
			}
		}
	}
}