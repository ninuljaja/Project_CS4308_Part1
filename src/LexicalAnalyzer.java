/**
 * LexicalAnalyzer Class
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class LexicalAnalyzer {
    private CharacterClass charClass;
    private ArrayList<Character> lexeme = new ArrayList<>();
    private char nextChar;
    private int lexLen;
    private Tokens nextToken;
    private FileReader reader = null;
    private int lineCount;
    private String[] keywordTablePy = {"def", "print","if","elif","else"};
    private String[] keywordTableJava = {"public", "class", "static", "void", "main", "String", "System", "out", "println", "int"};
    private ArrayList<String> listKeys = new ArrayList<>();



    /** Characters classes */
    private enum CharacterClass {
        LETTER(0),
        DIGIT(1),
        UNDERSCORE(2),
        EQUAL(3),
        UNKNOWN(99),
        EOF(-1);;

        private final int numValue;

        CharacterClass(int num) {
            numValue = num;
        }

        public int getNumValue() {
            return numValue;
        }
    }
    /** Tokens  */
    private enum Tokens {
        INT_LIT(10),
        IDENT(11),
        KEYWORD(12),
        ASSIGN_OP(20),
        ADD_OP(21),
        SUB_OP(22),
        MULT_OP(23),
        DIV_OP(24),
        LEFT_PAREN(25),
        RIGHT_PAREN(26),
        LEFT_BR(27),
        RIGHT_BR(28),
        LEFT_BRACKET(29),
        RIGHT_BRACKET(30),
        COMMA(31),
        SEMICOLON(32),
        DOT_OP(33),
        QUOT_MARK(34),
        COLON(35),
        SINGLE_QUOTE(36),
        UNDERSCORE(37),
        EQUALS(38),
        LESS_THAN(39),
        GREATER_THAN(40),
        EOF(-1);

        private final int numValue;
        Tokens(int num) {
            numValue = num;
        }

        public int getNumValue() {
            return numValue;
        }
    }

    /**
     * readFile method that opens the input data file, processes its contents, and counts total lines
     * @param file a String that holds a file name
     * @throws IOException if an I/O error occurs (file does not exist)
     */
    public void readFile(String file) throws IOException {
        // Open the input data file and process its contents
        try {
            reader = new FileReader(file);
            lineCount = 1;
            getChar();
            // read next lexeme until the end of file is reached
            do {
                lex();
            } while (nextToken != Tokens.EOF);
            // print out total lines in data file
            System.out.println("Total lines: " + lineCount);
        } catch (IOException ioex) {
            System.out.println("Error: cannot open " + file + ": "+ ioex.getMessage());
        }

        finally {
            reader.close();
        }
    }

    /**
     * lookup - a function to lookup operators, parentheses, brackets, and other tokens
     * @param ch a character
     * @return the corresponding token
     */
    public Tokens lookup(char ch) {
        // identify a character
        switch (ch) {
            case '(':
                addChar();
                return Tokens.LEFT_PAREN;
            case ')':
                addChar();
                return Tokens.RIGHT_PAREN;

            case '+':
                addChar();
                return Tokens.ADD_OP;

            case '-':
                addChar();
                return Tokens.SUB_OP;

            case '*':
                addChar();
                return Tokens.MULT_OP;

            case '/':
                addChar();
                return Tokens.DIV_OP;

            case '{':
                addChar();
                return Tokens.LEFT_BR;

            case '}':
                addChar();
                return Tokens.RIGHT_BR;

            case '[':
                addChar();
                return Tokens.LEFT_BRACKET;

            case ']':
                addChar();
                return Tokens.RIGHT_BRACKET;

            case '=':
                addChar();
                return Tokens.ASSIGN_OP;

            case ',':
                addChar();
                return Tokens.COMMA;

            case ';':
                addChar();
                return Tokens.SEMICOLON;

            case '.':
                addChar();
                return Tokens.DOT_OP;

            case '"':
                addChar();
                return Tokens.QUOT_MARK;

            case ':':
                addChar();
                return Tokens.COLON;

            case '\'':
                addChar();
                return Tokens.SINGLE_QUOTE;

            case '_':
                addChar();
                return Tokens.UNDERSCORE;
            case '<':
                addChar();
                return Tokens.LESS_THAN;
            case '>':
                addChar();
                return Tokens.GREATER_THAN;

            default:
                addChar();
                return Tokens.EOF;
        }
    }

    /**
     * addChar - a function to add nextChar to lexeme
     */
    public void addChar() {
        // Add nextChar to lexeme, with a check for lexeme length
        if (lexLen++ <= 98) {
            lexeme.add(nextChar);
        }
        else {
            System.out.println("Error - lexeme is too long\n");
        }
    }

    /**
     * getChar - a function to get the next character of
     *      input and determine its character class
     * @throws IOException if an I/O error occurs
     */
    public void getChar() throws IOException {
        // Read the next character
        int charNum = reader.read();
        nextChar = (char)charNum;
        // determine the character class (-1 is EOF)
        if (charNum != -1) {
            if (Character.isLetter(nextChar))
                charClass = CharacterClass.LETTER;
            else if (Character.isDigit(nextChar))
                charClass = CharacterClass.DIGIT;
            else if(nextChar == '_'){
                charClass = CharacterClass.UNDERSCORE;
            } else if(nextChar == '='){
                charClass = CharacterClass.EQUAL;
            }
            else {
                charClass = CharacterClass.UNKNOWN;
            }
        }
        else {
            charClass = CharacterClass.EOF;
        }
    }

    /**
     * getNonBlank - a function to call getChar until it
     *      returns a non-whitespace character
     * @throws IOException if an I/O error occurs
     */
    public void getNonBlank() throws IOException {
        // Skip whitespace characters, counting lines if newline is encountered
        while (Character.isWhitespace(nextChar)) {
            if(nextChar == '\n'){
                lineCount++;
            }
            getChar();
        }
    }

    /**
     * lex - a.00. lexical analyzer
     * @throws IOException if an I/O error occurs
     */
    public void lex() throws IOException {
        // Initialize lexeme and get the first non-whitespace character
        lexLen = 0;
        lexeme.removeAll(lexeme);
        getNonBlank();
        boolean isKeyJava = false;
        switch (charClass) {
            case UNDERSCORE:
            case LETTER:
                // Parse identifiers (keywords or general identifiers)
                addChar();
                getChar();
                while (charClass == CharacterClass.LETTER || charClass == CharacterClass.DIGIT || charClass == CharacterClass.UNDERSCORE) {
                    addChar();
                    getChar();
                }
                nextToken = Tokens.IDENT;
                String tester = lexeme.stream().map(String::valueOf).collect(Collectors.joining());
                // Check if the identifier is a keyword in Java or Python
                for (String key: keywordTableJava){
                    if (tester.equals(key)){
                        nextToken = Tokens.KEYWORD;
                        isKeyJava = true;
                        listKeys.add(tester);
                        break;
                    }
                }
                if (!isKeyJava) {
                    for (String key : keywordTablePy) {
                        if (tester.equals(key)) {
                            nextToken = Tokens.KEYWORD;
                            listKeys.add(tester);
                            break;
                        }
                    }
                }
                break;
            case DIGIT: //Parse integer literals
                addChar();
                getChar();
                while (charClass == CharacterClass.DIGIT) {
                    addChar();
                    getChar();
                }
                nextToken = Tokens.INT_LIT;
                break;
            case EQUAL: // parse '=' and '==' operators
                addChar();
                getChar();
                if (charClass == CharacterClass.EQUAL) {
                    addChar();
                    getChar();
                }
                String tester1 = lexeme.stream().map(String::valueOf).collect(Collectors.joining());
                // check if lexeme is '=' or '=='
                if(tester1.equals("=")){
                    nextToken = Tokens.ASSIGN_OP;
                } else{
                    nextToken = Tokens.EQUALS;
                }
                break;
            case UNKNOWN: //  Parse unknown characters using lookup method
                nextToken = lookup(nextChar);
                getChar();
                break;
            case EOF: // End of file
                nextToken = Tokens.EOF;
                lexeme.add('E');
                lexeme.add('O');
                lexeme.add('F');
                break;
        }
        // Print the result (next token and lexeme)
        System.out.printf("Next token is: %s (%d), Next lexeme is %s\n", nextToken,nextToken.getNumValue(),
                lexeme.stream().map(String::valueOf).collect(Collectors.joining()));
    }
}