package br.com.vex;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.vex.cliente.*;
import br.com.vex.clienteplano.*;
import br.com.vex.plano.*;

public class ProgramaPrincipal {
	
	static ConexaoBD connection = ConexaoBD.getInstance();

	public void menuPrincipal() {
		System.out.println("\n|---------- MENU PRINCIPAL ----------|");
		System.out.println("|1 -> Clientes                       |");
		System.out.println("|2 -> Planos                         |");
		System.out.println("|3 -> Clientes X Planos              |");
		System.out.println("|4 -> Finalizar Sistema              |");
		System.out.println("|------------------------------------|");

	}

	public static void main(String[] args) {

		Scanner tec;
		tec = new Scanner(System.in);

		List<Cliente> clientes = new ArrayList<>();
		List<Plano> planos = new ArrayList<>();
		List<ClientePlano> clientePlanos = new ArrayList<>();

		ClienteController clienteController = new ClienteController();
		PlanoController planoController = new PlanoController();
		ClientePlanoController clientePlanoController = new ClientePlanoController();



		boolean sair = false;

		do {

			ProgramaPrincipal programaPrincipal = new ProgramaPrincipal();
			programaPrincipal.menuPrincipal();
			System.out.println("\n");
			System.out.print("Informe a opção desejada -> ");
			int opcao = tec.nextInt();
			System.out.println("\n");

			switch (opcao) {

			case 1:
				clienteController.menuCliente(clientes, connection);
				break;

			case 2:
				planoController.menuPlano(planos, connection);
				break;

			case 3:
				clientePlanoController.menuClientePlano(clientePlanos, connection);
				break;
			case 4:
				sair = true;

				break;

			default:
				System.out.println("\n");
				System.out.println("Opção Inválida!!");
				System.out.println("\n");
				break;

			}

		} while (!sair);

		System.out.println("Sistema Finalizado!!!");

		tec.close();
	}
}
