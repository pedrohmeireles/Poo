package model;

import exceptions.CadastroDuplicadoException;
import exceptions.DadosInvalidosException;
import exceptions.RegistroNaoEncontradoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Coleção independente do banco. Utiliza a mesma entidade do projeto MVC. */
public class UsuarioColecao {
    private final Map<Integer, Usuario> usuarios = new HashMap<>();

    public void adicionar(Usuario usuario)
            throws DadosInvalidosException, CadastroDuplicadoException {
        if (usuario == null) throw new DadosInvalidosException("Informe um usuário.");
        validarId(usuario.getId());
        validarCampo(usuario.getNome(), "nome");
        validarCampo(usuario.getCurso(), "curso");
        validarCampo(usuario.getUniversidade(), "universidade");
        if (usuarios.containsKey(usuario.getId())) {
            throw new CadastroDuplicadoException("O ID " + usuario.getId() + " já está cadastrado.");
        }
        usuarios.put(usuario.getId(), copiar(usuario));
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) lista.add(copiar(usuario));
        return lista;
    }

    public Usuario buscarPorId(int id)
            throws DadosInvalidosException, RegistroNaoEncontradoException {
        validarId(id);
        Usuario usuario = usuarios.get(id);
        if (usuario == null) {
            throw new RegistroNaoEncontradoException("Não existe usuário com o ID " + id + ".");
        }
        return copiar(usuario);
    }

    public void removerPorId(int id)
            throws DadosInvalidosException, RegistroNaoEncontradoException {
        buscarPorId(id);
        usuarios.remove(id);
    }

    public int getQuantidade() { return usuarios.size(); }

    private void validarId(int id) throws DadosInvalidosException {
        if (id <= 0) throw new DadosInvalidosException("O ID deve ser maior que zero.");
    }

    private void validarCampo(String valor, String campo) throws DadosInvalidosException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DadosInvalidosException("Preencha o campo " + campo + ".");
        }
    }

    // Usuario possui setters para o ORM. As cópias protegem o estado da coleção.
    private Usuario copiar(Usuario original) {
        Usuario copia = new Usuario(original.getNome().trim(), original.getCurso().trim(),
                original.getUniversidade().trim());
        copia.setId(original.getId());
        return copia;
    }
}
