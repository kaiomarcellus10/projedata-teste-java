import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Principal {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormatSymbols PT_BR = new DecimalFormatSymbols(new Locale("pt", "BR"));
    private static final DecimalFormat MONEY_FMT;
    static {
        PT_BR.setDecimalSeparator(',');
        PT_BR.setGroupingSeparator('.');
        MONEY_FMT = new DecimalFormat("#,##0.00", PT_BR);
        MONEY_FMT.setParseBigDecimal(true);
    }

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");

    public static void main(String[] args) {
        List<Funcionario> funcionarios = seedFuncionarios();

        System.out.println("\n==== 3.1 Funcionarios inseridos (ordem original) ====");
        imprimirFuncionarios(funcionarios);

        funcionarios.removeIf(f -> f.getNome().equalsIgnoreCase("Joao"));
        System.out.println("\n==== 3.2 Apos remover 'Joao' ====");
        imprimirFuncionarios(funcionarios);

        System.out.println("\n==== 3.3 Impressao formatada ====");
        imprimirFuncionarios(funcionarios);

        funcionarios.forEach(f -> f.setSalario(percentIncrease(f.getSalario(), 10)));
        System.out.println("\n==== 3.4 Aumento de 10% nos salarios ====");
        imprimirFuncionarios(funcionarios);

        Map<String, List<Funcionario>> porFuncao = funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getFuncao));
        System.out.println("\n==== 3.5 Mapa por funcao ====");
        porFuncao.forEach((funcao, lista) -> System.out.println(funcao + " => " + lista.size()));

        System.out.println("\n==== 3.6 Funcionarios agrupados por funcao ====");
        porFuncao.forEach((funcao, lista) -> {
            System.out.println("\nFuncao: " + funcao);
            imprimirFuncionarios(lista);
        });

        System.out.println("\n==== 3.8 Aniversariantes em outubro e dezembro ====");
        funcionarios.stream()
                .filter(f -> f.getDataNascimento().getMonthValue() == 10 || f.getDataNascimento().getMonthValue() == 12)
                .forEach(System.out::println);

        System.out.println("\n==== 3.9 Funcionario mais velho ====");
        Optional<Funcionario> maisVelho = funcionarios.stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento));
        maisVelho.ifPresent(f -> {
            int idade = Period.between(f.getDataNascimento(), LocalDate.now()).getYears();
            System.out.println("Nome: " + f.getNome() + ", Idade: " + idade);
        });

        System.out.println("\n==== 3.10 Funcionarios em ordem alfabetica ====");
        funcionarios.stream()
                .sorted(Comparator.comparing(Funcionario::getNome, String.CASE_INSENSITIVE_ORDER))
                .forEach(System.out::println);

        System.out.println("\n==== 3.11 Soma total dos salarios ====");
        BigDecimal totalSalarios = funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total: R$ " + MONEY_FMT.format(totalSalarios));

        System.out.println("\n==== 3.12 Quantos salarios minimos cada funcionario recebe ====");
        funcionarios.forEach(f -> {
            BigDecimal qtd = f.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
            System.out.println(f.getNome() + " => " + MONEY_FMT.format(qtd));
        });
    }

    private static BigDecimal percentIncrease(BigDecimal base, int pct) {
        BigDecimal fator = BigDecimal.valueOf(1 + (pct / 100.0));
        return base.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    private static void imprimirFuncionarios(List<Funcionario> funcionarios) {
        funcionarios.forEach(System.out::println);
    }

    private static List<Funcionario> seedFuncionarios() {
        List<Funcionario> lista = new ArrayList<>();

        // Substituir pelos dados reais na ordem da tabela
        add(lista, "Maria", "18/10/2000", "2.009,44", "Operador");
        add(lista, "Joao", "12/05/1990", "2.284,38", "Operador");
        add(lista, "Caio", "02/05/1961", "9.836,14", "Coordenador");
        add(lista, "Miguel", "14/10/1988", "1.910,85", "Diretor");
        add(lista, "Alice", "05/01/1995", "2.233,88", "Recepcionista");
        add(lista, "Heitor", "19/11/1999", "1.709,45", "Operador");
        add(lista, "Helena", "02/12/1996", "3.245,68", "Gerente");
        add(lista, "Laura", "08/07/1994", "2.157,15", "Contadora");

        return lista;
    }

    private static void add(List<Funcionario> lista, String nome, String data, String salarioBR, String funcao) {
        LocalDate dt = LocalDate.parse(data, DTF);
        BigDecimal sal = parseBRL(salarioBR);
        lista.add(new Funcionario(nome, dt, sal, funcao));
    }

    private static BigDecimal parseBRL(String valor) {
        String normalized = valor.replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }
}

class Pessoa {
    private String nome;
    private LocalDate dataNascimento;

    public Pessoa(String nome, LocalDate dataNascimento) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
}

class Funcionario extends Pessoa {
    private BigDecimal salario;
    private String funcao;

    public Funcionario(String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        super(nome, dataNascimento);
        this.salario = salario.setScale(2, RoundingMode.HALF_UP);
        this.funcao = funcao;
    }

    public BigDecimal getSalario() { return salario; }
    public String getFuncao() { return funcao; }
    public void setSalario(BigDecimal salario) { this.salario = salario.setScale(2, RoundingMode.HALF_UP); }
    public void setFuncao(String funcao) { this.funcao = funcao; }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormatSymbols ptBr = new DecimalFormatSymbols(new Locale("pt", "BR"));
        ptBr.setDecimalSeparator(',');
        ptBr.setGroupingSeparator('.');
        DecimalFormat fmt = new DecimalFormat("#,##0.00", ptBr);
        return String.format("Nome: %s | Nascimento: %s | Salario: R$ %s | Funcao: %s",
                getNome(), getDataNascimento().format(dtf), fmt.format(getSalario()), getFuncao());
    }
}
