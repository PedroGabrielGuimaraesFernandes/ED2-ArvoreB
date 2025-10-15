class Pagina {
    int[] chaves;       // Vetor para armazenar as chaves
    Pagina[] filhos;    // Vetor de ponteiros para as páginas-filhas
    int numChaves;      // Número de chaves na página
    boolean ehFolha;    // Indica se a página é folha
    Pagina paginaPai;   // Referência para a página pai
    
    // Construtor para inicializar a página
    public Pagina(int ordem) {
        chaves = new int[ordem - 1];     // O número máximo de chaves é ordem-1
        filhos = new Pagina[ordem];      // O número máximo de filhos é ordem
        numChaves = 0;                   // Inicialmente, a página não tem chaves
        ehFolha = true;                  // Inicialmente, é folha
        paginaPai = null;                // Pai inicialmente nulo
    }

    // Métodos getters e setters para a página pai
    public Pagina getPaginaPai() {
        return paginaPai;
    }

    public void setPaginaPai(Pagina paginaPai) {
        this.paginaPai = paginaPai;
    }

    public boolean isFolha() {
        return ehFolha;
    }

    public void setFolha(boolean ehFolha) {
        this.ehFolha = ehFolha;
    }

    // Adiciona uma chave e ordena as chaves
    public void adicionarChave(int chave) {
        if (numChaves < chaves.length) {
            chaves[numChaves] = chave;
            numChaves++;
            ordenarChaves();
        }
    }

    private void ordenarChaves() {
        for (int i = 0; i < numChaves - 1; i++) {
            for (int j = i + 1; j < numChaves; j++) {
                if (chaves[i] > chaves[j]) {
                    int temp = chaves[i];
                    chaves[i] = chaves[j];
                    chaves[j] = temp;
                }
            }
        }
    }
}

class ArvoreB {
    private Pagina raiz;
    private int d;  // ordem mínima da árvore (d)
    
    public ArvoreB(int d) {
        this.d = d;
        raiz = new Pagina(2 * d); // ordem da página é 2d (máximo 2d chaves)
    }

    // Busca a página folha onde a chave deve estar/inserir
    private Pagina buscarPagina(Pagina pagina, int chave) {
        if (pagina.isFolha()) {
            return pagina;
        }
        int i = 0;
        while (i < pagina.numChaves && chave > pagina.chaves[i]) {
            i++;
        }
        return buscarPagina(pagina.filhos[i], chave);
    }

    // Inserção principal, chamada externamente
    public void inserir(int chave) {
        Pagina folha = buscarPagina(raiz, chave);
        inserirNaPagina(folha, chave);
    }

    // Inserir a chave na página (folha) e tratar cisão se necessário
    private void inserirNaPagina(Pagina pagina, int chave) {
        // Insere a chave na página
        pagina.adicionarChave(chave);

        // Se ultrapassou 2d chaves, precisa dividir
        if (pagina.numChaves > 2 * d) {
            split(pagina);
        }
    }

    // Dividir página cheia em duas páginas e promover a chave mediana
    private void split(Pagina pagina) {
        int meio = d; // índice da chave mediana
        
        // Criar duas novas páginas f1 e f2
        Pagina f1 = new Pagina(2 * d);
        Pagina f2 = new Pagina(2 * d);

        // f1 recebe s[0] ... s[d-1]
        for (int i = 0; i < meio; i++) {
            f1.adicionarChave(pagina.chaves[i]);
        }
        // f2 recebe s[d+1] ... s[2d]
        for (int i = meio + 1; i < pagina.numChaves; i++) {
            f2.adicionarChave(pagina.chaves[i]);
        }

        // Se a página original não for folha, distribuir filhos
        if (!pagina.isFolha()) {
            f1.ehFolha = false;
            f2.ehFolha = false;

            // filhos de 0 até meio (inclusive meio)
            for (int i = 0; i <= meio; i++) {
                f1.filhos[i] = pagina.filhos[i];
                if (f1.filhos[i] != null) f1.filhos[i].setPaginaPai(f1);
            }
            // filhos de meio+1 até fim
            for (int i = meio + 1; i <= pagina.numChaves; i++) {
                f2.filhos[i - (meio + 1)] = pagina.filhos[i];
                if (f2.filhos[i - (meio + 1)] != null) f2.filhos[i - (meio + 1)].setPaginaPai(f2);
            }
        }

        // A chave mediana a ser promovida para a página pai
        int chaveMediana = pagina.chaves[meio];

        // Se a página dividida é a raiz
        if (pagina == raiz) {
            Pagina novaRaiz = new Pagina(2 * d);
            novaRaiz.ehFolha = false;
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

        // Encontrar a posição onde a página f1 e f2 vão ficar no pai
        int pos = 0;
        while (pos < pai.numChaves && pai.filhos[pos] != pagina) {
            pos++;
        }

        // Mover filhos para abrir espaço para f2
        for (int i = pai.numChaves; i > pos; i--) {
            pai.filhos[i + 1] = pai.filhos[i];
        }
        pai.filhos[pos] = f1;
        pai.filhos[pos + 1] = f2;
        f1.setPaginaPai(pai);
        f2.setPaginaPai(pai);

        // Inserir chave mediana no pai
        // Para inserir chave mediana no pai, vamos usar uma função auxiliar
        inserirChavePai(pai, chaveMediana);

        // Se pai ultrapassou o limite, chamar split recursivamente
        if (pai.numChaves > 2 * d) {
            split(pai);
        }
    }

    // Inserir chave mediana no pai, mantendo chaves ordenadas
    private void inserirChavePai(Pagina pai, int chave) {
        // Inserir chave na próxima posição disponível
        pai.chaves[pai.numChaves] = chave;
        pai.numChaves++;
        // Ordenar chaves do pai
        for (int i = 0; i < pai.numChaves - 1; i++) {
            for (int j = i + 1; j < pai.numChaves; j++) {
                if (pai.chaves[i] > pai.chaves[j]) {
                    int temp = pai.chaves[i];
                    pai.chaves[i] = pai.chaves[j];
                    pai.chaves[j] = temp;
                }
            }
        }
    }

    // Método para imprimir a árvore para testes (em ordem)
    public void imprimir() {
        imprimirRec(raiz, 0);
    }

    private void imprimirRec(Pagina pagina, int nivel) {
        System.out.print("Nivel " + nivel + ": ");
        for (int i = 0; i < pagina.numChaves; i++) {
            System.out.print(pagina.chaves[i] + " ");
        }
        System.out.println();
        if (!pagina.isFolha()) {
            for (int i = 0; i <= pagina.numChaves; i++) {
                if (pagina.filhos[i] != null)
                    imprimirRec(pagina.filhos[i], nivel + 1);
            }
        }
    }
}
