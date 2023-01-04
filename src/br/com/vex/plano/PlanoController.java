package br.com.vex.plano;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.vex.ConexaoBD;
import br.com.vex.ProgramaPrincipal;

public class PlanoController {

	private Scanner tec;

	List<Plano> planos = new ArrayList<>();

	public PlanoController() {
		tec = new Scanner(System.in);
	}

	public int leOpcao() {
		System.out.println("\n");
		System.out.print("Informe a opção desejada -> ");
		return tec.nextInt();
	}

	ProgramaPrincipal programaPrincipal = new ProgramaPrincipal();

	ConexaoBD connection = ConexaoBD.getInstance();

	public void menuPlano(List<Plano> planos, ConexaoBD connection) {
		boolean sair = false;

		do {
			System.out.println("|----------- MENU PLANOS -----------|");
			System.out.println("|1 -> Cadastrar Plano               |");
			System.out.println("|2 -> Listar Planos                 |");
			System.out.println("|3 -> Retornar ao menu principal    |");
			System.out.println("|-----------------------------------|");

			int opcao = leOpcao();

			switch (opcao) {

			case 1:
				System.out.println("\n");
				planos.add(inserirPlano(connection, tec));
				break;

			case 2:
				System.out.println("\n");
				exibirPlanos(connection);;
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

	private static Plano inserirPlano(ConexaoBD connection, Scanner scanner) {
		scanner.nextLine();
		System.out.print("\nNome do Plano: ");
		String NomePlano = scanner.nextLine();
		System.out.print("Valor: ");
		double ValorPlano = scanner.nextDouble();

		if (ValorPlano <= 0) {
			System.out.println("\nO valor do plano não pode ser 0!");
		} else {
			// Executar comando SQL para inserir o plano
			String sql = "INSERT INTO TB_Planos (NomePlano, ValorPlano) VALUES (?, ?)";
			try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, NomePlano);
				statement.setDouble(2, ValorPlano);
				statement.executeUpdate();

				// Obter o código da chave primária gerado pelo banco de dados
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					int id = resultSet.getInt(1);
					System.out.println("\nPlano inserido com sucesso! \nCódigo da chave primária: " + id);
				}else {
					System.out.println("Falha ao inserir plano!");
				}
			}catch (SQLException e) {
				System.out.println("Falha ao inserir plano: " + e.getMessage());
			}
		}
		return null;
	}

	private static void exibirPlanos(ConexaoBD connection) {

		String sql = "SELECT * FROM TB_Planos";
		try (PreparedStatement statement = ConexaoBD.getInstance().getConnection().prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			// Check if there are any clients in the database
			if (!resultSet.next()) {
				System.out.println("Não há planos na base de dados.");
				return;
			}

			do {
				int IDPlano = resultSet.getInt("IDPlano");
				String NomePlano = resultSet.getString("NomePlano");
				double ValorPlano = resultSet.getDouble("ValorPlano");
				System.out.println(IDPlano + " - " + NomePlano + " - " + ValorPlano);
			} while (resultSet.next());
		} catch (SQLException e) {
			System.out.println("Falha ao exibir planos: " + e.getMessage());
		}
	}
}

