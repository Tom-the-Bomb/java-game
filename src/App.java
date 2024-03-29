import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.IOException;

public class App extends JPanel implements ActionListener {
    private GamePanel gamePanel;
    // number of rows and columns of the game board grid
    protected int rows;
    protected int cols;

    protected JLabel movesLabel;
    // inputs for setting the number of rows and columns
    private JLabel rowsLabel;
    private JTextField rowsInput;
    private JLabel colsLabel;
    private JTextField colsInput;

    // help and restart buttons
    private JButton help;
    private JButton restart;

    // help screen components
    private JButton back;

    // starting dimensions of the frame
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;

    // size constraint constants (inclusive)
    // i.e. maximum & minimum number of rows & columns allowed
    private static final int MIN_DIMS = 2;
    private static final int MAX_DIMS = 30;

    // text colors
    private static final Color TITLE_COLOR = new Color(255, 255, 255);
    protected static final Color LABEL_COLOR = new Color(170, 255, 200);
    protected static final Color BG_COLOR = new Color(60, 60, 70);
    // buttons colors
    private static final Color SECONDARY_BTN_COLOR = new Color(90, 90, 100);
    private static final Color DANGER_BTN_COLOR = Color.RED;

    // custom fonts that require loading in constructor
    private final Font TITLE_FONT;
    private final Font CODE_FONT;

    public App(int rows, int cols) throws FontFormatException, IOException {
        this.rows = rows;
        this.cols = cols;

        setBackground(BG_COLOR);

        TITLE_FONT = getFont("/fonts/BrownieStencil-vmrPE.ttf", 50);
        CODE_FONT = getFont("/fonts/FiraCodeNerdFont-Bold.ttf", 30);

        movesLabel = new JLabel("Moves: 0");
        movesLabel.setFont(CODE_FONT);
        movesLabel.setForeground(LABEL_COLOR);
        gamePanel = new GamePanel(this);

        setupComponents();

        back = new JButton("Back");
        back.setForeground(GamePanel.TEXT_COLOR);
        back.setBackground(SECONDARY_BTN_COLOR);
        setupComponentProperties(back);
    }

    // creates and adds all the default home/game screen components
    //
    private void setupComponents() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints constraints = getDefaultConstraints();

        JLabel title = new JLabel("Number Slider");
        title.setFont(TITLE_FONT);
        title.setForeground(TITLE_COLOR);

        constraints.gridy = 0;
        add(title, constraints);

        constraints.gridy = 1;
        add(movesLabel, constraints);

        addGamePanel();

        // the `controls` sub-panel in the last row of the app
        JPanel controls = new JPanel();
        controls.setLayout(
            new GridLayout(0, 3, 10, 10)
        );
        controls.setBackground(BG_COLOR);

        help = new JButton("Help");
        help.setForeground(GamePanel.TEXT_COLOR);
        help.setBackground(SECONDARY_BTN_COLOR);

        restart = new JButton("Restart");
        restart.setForeground(GamePanel.TEXT_COLOR);
        restart.setBackground(DANGER_BTN_COLOR);

        rowsInput = new JTextField(5);
        colsInput = new JTextField(5);

        rowsLabel = new JLabel("    # Rows:");
        colsLabel = new JLabel("    # Cols:");

        rowsLabel.setForeground(GamePanel.TEXT_COLOR);
        colsLabel.setForeground(GamePanel.TEXT_COLOR);

        for (JComponent component : new JComponent[] {
            help,
            rowsLabel,
            rowsInput,
            restart,
            colsLabel,
            colsInput,
        }) {
            setupComponentProperties(component);
            controls.add(component);
        }

        constraints.gridy = 3;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.NONE;

        add(controls, constraints);
    }

    // setups some default (repetitive) component properties
    // such as font, color, listener etc.
    //
    private void setupComponentProperties(JComponent component) {
        component.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
        component.setFont(
            CODE_FONT.deriveFont(20f)
        );

        if (component instanceof JButton) {
            ((JButton) component).addActionListener(this);
        }
        if (component instanceof JTextField) {
            ((JTextField) component).addActionListener(this);
        }
    }

    // loads in a custom font file (.ttf) as a `Font`:
    //
    // <https://stackoverflow.com/questions/71125231/how-to-set-the-size-of-a-font-from-a-file-in-swing>
    //
    private Font getFont(String path, int size) throws FontFormatException, IOException {
        return Font.createFont(
            Font.TRUETYPE_FONT,
            this.getClass().getResourceAsStream(path)
        ).deriveFont((float) size);
    }

    // returns an instance of `GridBagConstraints`
    // with default values for the properties that we desire for our panel
    //
    private GridBagConstraints getDefaultConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        return constraints;
    }

    // `app` is the outer container around the game tiles panel `gamePanel`
    // adds `gamePanel` to `app` with proper positioning
    //
    // adds an even 100px margin around the entire game
    // centerizes the game on the frame
    //
    // <https://stackoverflow.com/questions/30611975/giving-a-jpanel-a-percentage-based-width>
    //
    // `app` uses `GridBagLayout` to handle all this well.
    private void addGamePanel() {
        GridBagConstraints constraints = getDefaultConstraints();
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 50, 0, 50);

        add(gamePanel, constraints);
    }

    // validates the inputs of the `row` and `cols`
    // ensuring they are not less than `MIN_DIMS` or greater than `MAX_DIM`
    // displaying a message box if they are
    //
    public boolean validateInput(int newSize) {
        if (newSize < MIN_DIMS || newSize > MAX_DIMS) {
            // Shows a message box like javascript's `alert(...)` in the web:
            //
            // <https://stackoverflow.com/questions/7080205/popup-message-boxes>
            //
            JOptionPane.showInternalMessageDialog(
                null,
                String.format("The number of rows must be between %d and %d.",
                    MIN_DIMS,
                    MAX_DIMS
                )
            );
            return false;
        }
        return true;
    }

    public void actionPerformed(ActionEvent event) {
        Object component = event.getSource();

        if (
            component == restart
            || (component == rowsInput && !rowsInput.getText().isEmpty())
            || (component == colsInput && !colsInput.getText().isEmpty())
        ) {
            try {
                String rawRows = rowsInput.getText();
                String rawCols = colsInput.getText();

                if (!rawRows.isEmpty() ) {
                    int newRows = Integer.parseInt(rawRows);
                    if (!validateInput(newRows)) {
                        return;
                    }
                    rows = newRows;
                }
                if (!rawCols.isEmpty() ) {
                    int newCols = Integer.parseInt(rawCols);
                    if (!validateInput(newCols)) {
                        return;
                    }
                    cols = newCols;
                }
            } catch (NumberFormatException err) {
                JOptionPane.showInternalMessageDialog(
                    null, "Invalid numerical input value"
                );
                return;
            }
            remove(gamePanel);

            movesLabel.setText("Moves: 0");
            gamePanel = new GamePanel(this);
            addGamePanel();

        } else if (component == help) {
            removeAll();

            JLabel title = new JLabel("How to Play");
            title.setFont(TITLE_FONT);
            title.setForeground(GamePanel.TEXT_COLOR);
            title.setHorizontalAlignment(JLabel.CENTER);

            GridBagConstraints constraints = getDefaultConstraints();

            constraints.gridy = 0;
            add(title, constraints);

            JTextPane description = new JTextPane();
            description.setContentType("text/html");
            description.setEditable(false);
            description.setText(
                String.join(
                    System.lineSeparator(),
                    "<html>",
                    "    <div style=\"",
                    String.format(
                        "        color: rgb(%d, %d, %d);",
                        LABEL_COLOR.getRed(),
                        LABEL_COLOR.getGreen(),
                        LABEL_COLOR.getRed()
                    ),
                    "        font-size: 20pt;",
                    "        font-weight: 200;",
                    "    \">",
                    "        <pre>",
                    "The goal of the game is to sort all the tiles in ascending order.",
                    "",
                    "The desired endgame layout is to have the top-left corner be a <b>[1]</b>",
                    "and in <b>ascending</b> order all the way to the bottom-left which should be the <b>[largest number]</b>",
                    "",
                    "The dark gray tile represents the <b>empty</b> tile that its neighbors can swap to.",
                    "Therefore, only the direct <b>neighbors</b> of that tile can be <b>clicked</b>",
                    "and said tile will get <b>swapped</b> with the blank tile when <b>clicked</b>",
                    "",
                    "<b>Note:</b> All tiles that are in the <b>correct</b> position will be colored <b>green</b> instead",
                    "        </pre>",
                    "        <hr>",
                    "        <pre>",
                    "Click <b>[Restart]</b> to generate a random fresh board (and to update grid size values)",
                    "",
                    String.format(
                        "Enter <b>[# Rows]</b> (number from %d to %d) to change the number of rows of the grid",
                        MIN_DIMS, MAX_DIMS
                    ),
                    String.format(
                        "Enter <b>[# Cols]</b> (number from %d to %d) to change number of columns of the grid",
                        MIN_DIMS, MAX_DIMS
                    ),
                    "(By default the grid size is <b>4x4</b>)",
                    "",
                    "<i>* All generated puzzles are guaranteed solvable</i>",
                    "",
                    "<b>Have Fun!</b>",
                    "",
                    String.format(
                        "[Running on Java v%s]",
                        System.getProperty("java.version")
                    ),
                    "        </pre>",
                    "    </div>",
                    "</html>"
                )
            );
            description.setOpaque(false);

            constraints.gridy = 1;
            constraints.fill = GridBagConstraints.NONE;
            add(description, constraints);

            constraints.gridy = 2;
            add(back, constraints);

        } else if (component == back) {
            removeAll();
            setupComponents();

        } else {
            return;
        }

        revalidate();
        repaint();
    }

    public static void main(String[] args) throws FontFormatException, IOException {
        JFrame frame = new JFrame("Number Slider");

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(
            new JScrollPane(
                new App(4, 4),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            )
        );
        frame.setVisible(true);
    }
}