import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Produto {
    private String nome;
    private String validade;

    public Produto(String nome, String validade) {
        this.nome = nome;
        this.validade = validade;
    }

    public String getNome() {
        return nome;
    }

    public String getValidade() {
        return validade;
    }
}

public class ControleValidadeProdutos extends Application {

    private List<Produto> produtos;
    private ObjectMapper objectMapper;

    private TextField nomeField;
    private TextField validadeField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        carregarProdutos();

        primaryStage.setTitle("Controle de Validade - Produtos");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        nomeField = new TextField();
        nomeField.setPromptText("Nome do Produto");
        GridPane.setConstraints(nomeField, 0, 0);

        validadeField = new TextField();
        validadeField.setPromptText("Data de Validade");
        GridPane.setConstraints(validadeField, 1, 0);

        Button adicionarButton = new Button("Adicionar Produto");
        adicionarButton.setOnAction(e -> adicionarProduto());
        GridPane.setConstraints(adicionarButton, 0, 1);

        Button listarButton = new Button("Listar Produtos");
        listarButton.setOnAction(e -> listarProdutos());
        GridPane.setConstraints(listarButton, 1, 1);

        grid.getChildren().addAll(nomeField, validadeField, adicionarButton, listarButton);

        Scene scene = new Scene(grid, 400, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void adicionarProduto() {
        String nome = nomeField.getText();
        String validade = validadeField.getText();

        Produto produto = new Produto(nome, validade);
        produtos.add(produto);

        salvarProdutos();
        limparCampos();
    }

    private void listarProdutos() {
        Stage stage = new Stage();
        stage.setTitle("Lista de Produtos");

        ListView<String> listView = new ListView<>();
        for (Produto produto : produtos) {
            listView.getItems().add(produto.getNome());
        }

        listView.setOnMouseClicked(event -> {
            String selectedProduct = listView.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                mostrarValidade(selectedProduct);
            }
        });

        Scene scene = new Scene(listView, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarValidade(String nomeProduto) {
        for (Produto produto : produtos) {
            if (produto.getNome().equals(nomeProduto)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validade do Produto");
                alert.setHeaderText(null);
                alert.setContentText("Produto: " + produto.getNome() + "\nValidade: " + produto.getValidade());
                alert.showAndWait();
                break;
            }
        }
    }

    private void salvarProdutos() {
        try {
            objectMapper.writeValue(new File("produtos.json"), produtos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarProdutos() {
        try {
            File file = new File("produtos.json");
            if (file.exists()) {
                produtos = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Produto.class));
            } else {
                produtos = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        nomeField.clear();
        validadeField.clear();
    }
}
