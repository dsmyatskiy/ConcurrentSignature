import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Gui implements ActionListener {
    String directory;
    String dataFilePath;

    JFrame frame = new JFrame("Подписи");
    JFrame parentFrame = new JFrame();
    JPanel panel = new JPanel();
    JButton signFileButton = new JButton("Подписать файл");
    JButton verifySignatureButton = new JButton("Проверить подпись");
    JButton selectDirectory = new JButton("Выбрать рабочую директорию");
    JButton selectDataFile = new JButton("Выбрать файл");
    JTextField fpName = new JTextField("Имя первого участника", 16);
    JTextField spName = new JTextField("Имя второго участника", 16);
    JTextField result = new JTextField();
    JFileChooser dirChooser = new JFileChooser();
    JFileChooser fileChooser = new JFileChooser();

    public static void main(String[] args) {
        new Gui();
    }

    public Gui() {
        dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        signFileButton.addActionListener(this);
        verifySignatureButton.addActionListener(this);
        fpName.addActionListener(this);
        spName.addActionListener(this);
        selectDirectory.addActionListener(this);
        selectDataFile.addActionListener(this);
        result.setEditable(false);

        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 5, 30));
        panel.setLayout(new GridLayout(5, 1));
        panel.add(selectDirectory);
        panel.add(selectDataFile);
        panel.add(fpName);
        panel.add(spName);
        panel.add(signFileButton);
        panel.add(verifySignatureButton);
        panel.add(result).setSize(new Dimension(10, 30));

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signFileButton) {
            try {
                if (new SignManager(fpName.getText(), spName.getText(), directory).signAll(dataFilePath))
                    result.setText("Файл успешно подписан!");
                else result.setText("Возникли проблемы при подписании");
            } catch (NullPointerException nullPointerException) {
                result.setText("Укажите файл!");
            }
        } else if (e.getSource() == verifySignatureButton) {
            try {
                if (new SignManager(fpName.getText(), spName.getText(), directory).verifyAll(dataFilePath))
                    result.setText("Подпись верна");
                else result.setText("Подпись не совпадает!");
            } catch (NullPointerException nullPointerException) {
                result.setText("Укажите файл!");
            }
        } else if (e.getSource() == selectDirectory) {
            dirChooser.setDialogTitle("Выбрать директорию");
            dirChooser.showOpenDialog(parentFrame);
            try {
                directory = dirChooser.getSelectedFile().getPath();
                System.out.println(directory);
            } catch (NullPointerException nullPointerException) {
                result.setText("Укажите директорию!");
            }
        } else if (e.getSource() == selectDataFile) {
            fileChooser.setDialogTitle("Выбрать файл");
            fileChooser.showOpenDialog(parentFrame);
            try {
                dataFilePath = fileChooser.getSelectedFile().getPath();
            } catch (NullPointerException nullPointerException) {
                result.setText("Укажите файл!");
            }
        }
    }
}
