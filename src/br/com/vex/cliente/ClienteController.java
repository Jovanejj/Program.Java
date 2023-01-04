package br.com.vex.cliente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.vex.ConexaoBD;
import br.com.vex.ProgramaPrincipal;

public class ClienteController {

	private Scanner tec;

	List<Cliente> clientes = new ArrayList<>();

	public ClienteController() {
		tec = new Scanner(System.in);
	}

	public int leOpcao() {
		System.out.println("\n");
		System.out.print("Informe a opção desejada -> ");
		return tec.nextInt();
	}

	ProgramaPrincipal programaPrincipal = new ProgramaPrincipal();

	ConexaoBD connection = ConexaoBD.getInstance();

	public void menuCliente(List<Cliente> clientes, ConexaoBD connection) {

		boolean sair = false;

		do {
			System.out.println("|---------- MENU CLIENTES ----------|");
			System.out.println("|1 -> Cadastrar Clientes            |");
			System.out.println("|2 -> Lista Clientes                |");
			System.out.println("|3 -> Retornar ao menu principal    |");
			System.out.println("|-----------------------------------|");

			int opcao = leOpcao();

			switch (opcao) {

			case 1:
				System.out.println("\n");
				clientes.add(inserirCliente(connection, tec));
				break;

			case 2:
				System.out.println("\n");
				exibirClientes(connection);
				break;

			case 3:
				break;

			default:
				System.out.println("Opção Inválida!!!");
				break;
			}

			System.out.print("\nDeseja retornar ao MENU PRINCIPAL? [S/N] -> ");
			String resposta = tec.next();
			sair = resposta.equalsIgnoreCase("n") ? true : false;
			System.out.println("\n");

		} while (sair);
	}

	private static Cliente inserirCliente(ConexaoBD connection, Scanner scanner) {

		System.out.print("Insira o nome do cliente: ");
		String NomeCliente = scanner.next();
		System.out.print("Insira o endereço: ");
		String Endereco = scanner.next();
		scanner.nextLine();
		System.out.print("Insira o e-mail: ");
		String Email = scanner.next();

		String sql = "INSERT INTO TB_Clientes (NomeCliente, Endereco, Email) VALUES (?, ?, ?)";
		try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, NomeCliente);
			statement.setString(2, Endereco);
			statement.setString(3, Email);
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				int id = resultSet.getInt(1);
				System.out.println("\nCliente inserido com sucesso! \nCódigo da chave primária: " + id);
			} else {
				System.out.println("Falha ao inserir cliente!");
			}
		} catch (SQLException e) {
			System.out.println("Falha ao inserir cliente: " + e.getMessage());
		}
		return null;
	}

	private static void exibirClientes(ConexaoBD connection) {

		String sql = "SELECT * FROM TB_Clientes";
		try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {

			if (!resultSet.next()) {
				System.out.println("Não há clientes na base de dados.");
				return;
			}

			do {
				int IDCliente = resultSet.getInt("IDCliente");
				String NomeCliente = resultSet.getString("NomeCliente");
				String Endereco = resultSet.getString("Endereco");
				String Email = resultSet.getString("Email");
				System.out.println(IDCliente + " - " + NomeCliente + " - " + Endereco + " - " + Email);
			} while (resultSet.next());
		} catch (SQLException e) {
			System.out.println("Falha ao exibir clientes: " + e.getMessage());
		}
	}
}