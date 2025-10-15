// ================== CLASSE PAGINA ==================
class Pagina {
    int[] chaves;
    int m; 
    Pagina[] filhos;
    boolean folha;
    Pagina paginaPai;

    public Pagina(int ordem) {
        chaves = new int[2 * ordem + 1];   // espaço extra para overflow (2d + 1)
        filhos = new Pagina[2 * ordem + 2];
        m = 0;
        folha = true;
        paginaPai = null;
    }

    public Pagina getPaginaPai() {
        return paginaPai;
    }

    public void setPaginaPai(Pagina paginaPai) {
        this.paginaPai = paginaPai;
    }

    public boolean isFolha() {
        return folha;
    }

    public void setFolha(boolean ehFolha) {
        this.folha = ehFolha;
    }

    public void adicionarChave(int chave) {
        chaves[m++] = chave;
        ordenarChaves();
    }

    private void ordenarChaves() {
        for (int i = 0; i < m - 1; i++) {
            for (int j = i + 1; j < m; j++) {
                if (chaves[i] > chaves[j]) {
                    int temp = chaves[i];
                    chaves[i] = chaves[j];
                    chaves[j] = temp;
                }
            }
        }
    }
}

// ================== CLASSE ÁRVORE B ==================
class ArvoreB {
    Pagina raiz;
    int ordem;

    public ArvoreB(int ordem) {
        this.ordem = ordem;
        raiz = new Pagina(ordem);
    }

    // Busca a página folha onde a chave deve ser inserida
    private Pagina buscarPagina(Pagina pagina, int chave) {
        if (pagina.isFolha()) {
            return pagina;
        }
        int i = 0;
        while (i < pagina.m && chave > pagina.chaves[i]) {
            i++;
        }
        return buscarPagina(pagina.filhos[i], chave);
    }

    // Inserção principal
    public void inserir(int chave) {
        Pagina folha = buscarPagina(raiz, chave);
        inserirNaPagina(folha, chave);
    }

    // Inserir chave e dividir se necessário
    private void inserirNaPagina(Pagina pagina, int chave) {
        pagina.adicionarChave(chave);
        if (pagina.m >= 2 * ordem + 1) {  // divide se passou do limite
            split(pagina);
        }
    }

    // Dividir página cheia
    private void split(Pagina pagina) {
        int meio = ordem;

        Pagina f1 = new Pagina(ordem);
        Pagina f2 = new Pagina(ordem);

        // Copia chaves para as novas páginas
        for (int i = 0; i < meio; i++) {
            f1.adicionarChave(pagina.chaves[i]);
        }
        for (int i = meio + 1; i < pagina.m; i++) {
            f2.adicionarChave(pagina.chaves[i]);
        }

        f1.setFolha(pagina.isFolha());
        f2.setFolha(pagina.isFolha());

        // Distribui filhos, se não for folha
        if (!pagina.isFolha()) {
            for (int i = 0; i <= meio; i++) {
                f1.filhos[i] = pagina.filhos[i];
                if (f1.filhos[i] != null) f1.filhos[i].setPaginaPai(f1);
            }
            for (int i = meio + 1; i <= pagina.m; i++) {
                f2.filhos[i - (meio + 1)] = pagina.filhos[i];
                if (f2.filhos[i - (meio + 1)] != null) f2.filhos[i - (meio + 1)].setPaginaPai(f2);
            }
        }

        int chaveMediana = pagina.chaves[meio];

        // Se a página dividida é a raiz
        if (pagina == raiz) {
            Pagina novaRaiz = new Pagina(ordem);
            novaRaiz.setFolha(false);
            novaRaiz.adicionarChave(chaveMediana);
            novaRaiz.filhos[0] = f1;
            novaRaiz.filhos[1] = f2;
            f1.setPaginaPai(novaRaiz);
            f2.setPaginaPai(novaRaiz);
            raiz = novaRaiz;
            return;
        }

        // Caso contrário, promover a chave para o pai
        Pagina pai = pagina.getPaginaPai();

        int pos = 0;
        while (pos < pai.m && pai.filhos[pos] != pagina) {
            pos++;
        }

        for (int i = pai.m; i > pos; i--) {
            pai.filhos[i + 1] = pai.filhos[i];
        }

        pai.filhos[pos] = f1;
        pai.filhos[pos + 1] = f2;
        f1.setPaginaPai(pai);
        f2.setPaginaPai(pai);

        inserirChavePai(pai, chaveMediana);

        if (pai.m >= 2 * ordem + 1) {
            split(pai);
        }
    }

    // Inserir chave no pai mantendo ordenação
    private void inserirChavePai(Pagina pai, int chave) {
        pai.chaves[pai.m++] = chave;
        for (int i = 0; i < pai.m - 1; i++) {
            for (int j = i + 1; j < pai.m; j++) {
                if (pai.chaves[i] > pai.chaves[j]) {
                    int temp = pai.chaves[i];
                    pai.chaves[i] = pai.chaves[j];
                    pai.chaves[j] = temp;
                }
            }
        }
    }

    // Impressão da árvore
    public void imprimir() {
        imprimirRec(raiz, 0);
    }

    private void imprimirRec(Pagina pagina, int nivel) {
        System.out.print("Nível " + nivel + ": ");
        for (int i = 0; i < pagina.m; i++) {
            System.out.print(pagina.chaves[i] + " ");
        }
        System.out.println();
        if (!pagina.isFolha()) {
            for (int i = 0; i <= pagina.m; i++) {
                if (pagina.filhos[i] != null)
                    imprimirRec(pagina.filhos[i], nivel + 1);
            }
        }
    }
}

// ================== MAIN ==================
public class Main {
    public static void main(String[] args) {
        ArvoreB arvore = new ArvoreB(2);

        int[] chaves = {10, 20, 5, 6, 12, 30, 7, 17};

        System.out.println("Inserindo chaves na árvore B:");
        for (int chave : chaves) {
            System.out.println("Inserindo " + chave + "...");
            arvore.inserir(chave);
            arvore.imprimir();
            System.out.println("-------------------------------");
        }

        System.out.println("\nÁrvore final:");
        arvore.imprimir();
    }
}
