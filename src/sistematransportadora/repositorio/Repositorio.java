package sistematransportadora.repositorio;

import java.util.HashMap;

/**
 * Interface que representa um repositório.
 * Aqui se define os métodos que cada repositório precisa ter para fazer
 * a comunicação com o banco de dados.
 *
 * @param <T> O tipo com que o repositório trabalha.
 */
public interface Repositorio<T> {
    // Create
    void criarNovo(T obj);

    // Read
    T buscarPorId(int id);
    T[] buscarTodos();
    HashMap<String, Integer> contarPorTipo();
    int contarTodos();
    boolean existeId(int id);

    // Update
    void atualizarUm(T obj);

    // Delete
    void apagarPorId(int id);
}
