import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class PartyCam extends JPanel implements Runnable
{
    public static int WIDTH = 0;
    public static int HEIGHT = 0;

    private BufferedImage image = null;

    private void renderText(Graphics g)
    {

    }

    public PartyCam()
    {
         Thread t = new Thread(this);
         t.start();
    }


    int tick = 0;
    int max = 60;
    public void run()
    {
        takePick("image.jpg");
	try{
            while (true) {
                tick++;
                if(tick > max) {
                    takePick("image.jpg");
                    tick = 0;
                }
		if(tick < 5){
                try {
                    image = ImageIO.read(new File("image.jpg"));
                } catch (IOException e) {
                	e.printStackTrace();
		}
}
                repaint();
                Thread.sleep(1000);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics gIn) {
        super.paintComponent(gIn);
        BufferedImage outImg = new BufferedImage(WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D)outImg.createGraphics();
        try {
            int y = 0;
            int x = 0;
	    if(image != null)
		{
            g2d.drawImage(image, // draw it
                    WIDTH/2 - (image.getWidth()/  2), HEIGHT/2 - (image.getHeight() /2),
                    null);
		}
            int left = max - tick;
            if (left < 20) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, getHeight() - 150, WIDTH, HEIGHT);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(5, getHeight() - 145, WIDTH - 5, HEIGHT - 5);
                g2d.setFont(g2d.getFont().deriveFont(60f));
                g2d.setColor(Color.BLACK);
                g2d.drawString("Next Pic in " + left + " seconds.", 20, getHeight() - 60);
            }



        }finally {
            g2d.dispose();
        }
        gIn.drawImage(outImg,0,0,WIDTH,HEIGHT,null);
    }



    private BufferedImage resize(BufferedImage source,
           int width, int height) {
	if(source == null)return source;
        Image img2 = source.getScaledInstance(width, height,
                Image.SCALE_AREA_AVERAGING);
        BufferedImage img = new BufferedImage(width, height,
                source.getType());
        Graphics2D g = img.createGraphics();
        try {
            g.drawImage(img2, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return img;
    }

    private void takePick(String name)
    {
        try {
            //ffmpeg.exe -y -f vfwcap -r 25 -i 0 image.jpg
            String[] windows = new String[]{"ffmpeg.exe", "-y", "-f", "vfwcap", "-r", "25", "-i", "0", name};
            String[] linux = new String[]{"fswebcam", "--no-banner", "-r", "1824x984", name};
            Process p = Runtime.getRuntime().exec(linux);
		p.waitFor();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws  Exception
    {
        JFrame frame = new JFrame();
        PartyCam partyCam = new PartyCam();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(partyCam, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        partyCam.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        HEIGHT = frame.getHeight();
        WIDTH = frame.getWidth();

    }



}

