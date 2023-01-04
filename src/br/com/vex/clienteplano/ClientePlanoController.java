package br.com.vex.clienteplano;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import br.com.vex.ConexaoBD;
import br.com.vex.ProgramaPrincipal;

public class ClientePlanoController {

	private Scanner tec;

	List<ClientePlano> clientePlanos = new ArrayList<>();

	public ClientePlanoController() {
		tec = new Scanner(System.in);
	}

	public int leOpcao() {
		System.out.println("\n");
		System.out.print("Informe a opção desejada -> ");
		return tec.nextInt();
	}

	ProgramaPrincipal programaPrincipal = new ProgramaPrincipal();

	ConexaoBD connection = ConexaoBD.getInstance();

	public void menuClientePlano(List<ClientePlano> clientePlanos, ConexaoBD connection) {
		boolean sair = false;

		do {
			System.out.println("|--------- MENU ASSOCIAÇÕES ---------|");
			System.out.println("|1 -> Associar Plano ao Cliente      |");
			System.out.println("|2 -> Listar Associações             |");
			System.out.println("|3 -> Retornar ao menu principal     |");
			System.out.println("|------------------------------------|");

			int opcao = leOpcao();

			switch (opcao) {

			case 1:
				System.out.println("\n");
				clientePlanos.add(associarClientePlano(connection, tec));
				break;

			case 2:
				System.out.println("\n");
				exibirAssociacoes(connection);
				break;

			case 3:
				programaPrincipal.menuPrincipal();
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

	private static ClientePlano associarClientePlano(ConexaoBD connection, Scanner scanner) {

		// Solicitar informações da associação ao usuário
		System.out.print("\nCódigo do cliente: ");
		int IDCliente = scanner.nextInt();

		// Buscar o nome do cliente a partir do código informado
		String nomeCliente = "";
		String sql = "SELECT NomeCliente FROM TB_Clientes WHERE IDCliente = ?";
		try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql)) {
			statement.setInt(1, IDCliente);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					nomeCliente = resultSet.getString("NomeCliente");
				}
			}
		} catch (SQLException e) {
			System.out.println("\nFalha ao buscar o nome do cliente: " + e.getMessage());
		}

		// Exibir o nome do cliente ou uma mensagem de erro caso o código seja inválido
		if (nomeCliente.isEmpty()) {
			System.out.println("\nCódigo de cliente inválido!");
		} else {
			System.out.println("Nome do cliente: " + nomeCliente);
		}

		System.out.print("\nCódigo do plano: ");
		int IDPlano = scanner.nextInt();

		// Buscar o nome e o valor do plano a partir do código informado
		String nomePlano = "";
		float valorPlano = 0;
		sql = "SELECT NomePlano, ValorPlano FROM TB_Planos WHERE IDPlano = ?";
		try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql)) {
			statement.setInt(1, IDPlano);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					nomePlano = resultSet.getString("NomePlano");
					valorPlano = resultSet.getFloat("ValorPlano");
				}
			}
		} catch (SQLException e) {
			System.out.println("\nFalha ao buscar o nome e valor do plano: " + e.getMessage());
		}
		if (nomePlano.isEmpty()) {
			System.out.println("\nCódigo de plano inválido!");
		} else {
			System.out.println("Nome do plano: " + nomePlano);
			System.out.println("Valor do plano: " + valorPlano);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		System.out.print("\nData de contratação: ");
		String dataContratacaoString = scanner.next();
		scanner.nextLine();
		System.out.print("Informe a data de cancelamento, caso não houver, bastar pressionar 'Enter': ");
		String dataCancelamentoString = scanner.nextLine();

		Date dataContratacao = null;
		Date dataCancelamento = null;

		try {
			dataContratacao = dateFormat.parse(dataContratacaoString);
		} catch (ParseException e) {
			System.out.println("Data de contratação inválida: " + e.getMessage());
		}

		if (!dataCancelamentoString.isEmpty()) {
			try {
				dataCancelamento = dateFormat.parse(dataCancelamentoString);
			} catch (ParseException e) {
				System.out.println("Data de cancelamento inválida: " + e.getMessage());
			}
		}

		// Verificar se os códigos de cliente e de plano são válidos antes de inserir a associação na tabela
		if (nomeCliente.isEmpty() || nomePlano.isEmpty()) {
			System.out.println("\nFalha ao realizar associação: código de cliente ou plano inválido!");
		} else {
			// Executar comando SQL para inserir a associação
			sql = "INSERT INTO TB_Cliente_Plano (IDCliente, IDPlano, Contratacao, Cancelamento) VALUES (?, ?, ?, ?)";
			try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql)) {
				statement.setInt(1, IDCliente);
				statement.setInt(2, IDPlano);
				statement.setDate(3, new java.sql.Date(dataContratacao.getTime()));
				if (dataCancelamento == null) {
					statement.setNull(4, Types.DATE);
				} else {
					statement.setDate(4, new java.sql.Date(dataCancelamento.getTime()));
				}
				statement.executeUpdate();
				System.out.println("\nAssociação realizada com sucesso!");
			} catch (SQLException e) {
				System.out.println("\nFalha ao realizar associação: " + e.getMessage());
			}
		}
		return null;
	}
	
	private static void exibirAssociacoes(ConexaoBD connection) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		// Executar comando SQL para selecionar as associações
		String sql = "SELECT c.NomeCliente, p.NomePlano, p.ValorPlano, cp.Contratacao, cp.Cancelamento FROM TB_Clientes c INNER JOIN TB_Cliente_Plano cp ON cp.IDCliente = c.IDCliente INNER JOIN TB_Planos p ON cp.IDPlano = p.IDPlano";
		try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			// Check if there are any clients in the database
			if (!resultSet.next()) {
				System.out.println("Não há planos na base de dados.");
				return;
			}
			// Exibir os resultados da consulta
			do {
				String NomeCliente = resultSet.getString("NomeCliente");
				String NomePlano = resultSet.getString("NomePlano");
				double ValorPlano = resultSet.getDouble("ValorPlano");
				Date dataContratacao = resultSet.getDate("Contratacao");
				String dataContratacaoFormatada = dateFormat.format(dataContratacao);
				Date dataCancelamento = resultSet.getDate("Cancelamento");
				String dataCancelamentoFormatada = dataCancelamento == null ? "Ativo" : dateFormat.format(dataCancelamento);
				System.out.println(NomeCliente + " - " + NomePlano + " - " + ValorPlano + " - " + dataContratacaoFormatada + " - " + dataCancelamentoFormatada);
			} while (resultSet.next());
		} catch (SQLException e) {
			System.out.println("\nFalha ao exibir associações: " + e.getMessage());
		}
	}
}