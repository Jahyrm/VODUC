/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wwecuador.voduc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.co.caprica.vlcj.binding.internal.libvlc_logo_position_e;
import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.Logo;
import uk.co.caprica.vlcj.player.Marquee;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;

/**
 *
 * @author Jahyr
 */
public class VentanaReproductor extends javax.swing.JFrame {

    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private boolean Reproduciendo = true;
    private JFrame este = this;
    private boolean dobleClick = false;
    private boolean seMovio = true;
    private boolean llamadaProgramada=false;
    private int valorActualSlider = 0;
    private int volumen = 100;
    private boolean panelVolumenVisible = false;
    private long longitud;
    private boolean seLongitud = false;
    private String titulo;
    /**
     * Creates new form MainWindow
     */
    public VentanaReproductor(String[] args) {
        new NativeDiscovery().discover();
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (mediaPlayerComponent.getMediaPlayer().isFullScreen()) {
                        fullScreen(false);
                    } else {
                        fullScreen(true);
                    }
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                if (seMovio) {
                    seMovio=false;
                    if (!Controles.isVisible()) {
                        ControlesArriba.setVisible(true);
                        Controles.setVisible(true);
                    }
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                if (Controles.isVisible()) {
                                    if (panelVolumenVisible) {
                                        PanelVolumen.setVisible(false);
                                        panelVolumenVisible = false;
                                    }
                                    Controles.setVisible(false);
                                    ControlesArriba.setVisible(false);
                                }
                                seMovio=true;
                            } catch (InterruptedException v) {
                                System.out.println(v);
                            }
                        }
                    }.start();
                }
                /*if (!Controles.isVisible()) {
                    ControlesArriba.setVisible(true);
                    Controles.setVisible(true);
                }
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            if (Controles.isVisible()) {
                                Controles.setVisible(false);
                                ControlesArriba.setVisible(false);
                            }
                        } catch (InterruptedException v) {
                            System.out.println(v);
                        }
                    }
                }.start();*/
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    mediaPlayerComponent.getMediaPlayer().pause();
                    if (Reproduciendo) {
                        botonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/play.png")));
                        Reproduciendo = false;
                    } else {
                        botonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause.png")));
                        Reproduciendo = true;
                    }
                } else if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    if (mediaPlayerComponent.getMediaPlayer().isFullScreen()){
                        fullScreen(false);
                    }
                }
            }
        };
        initComponents();
        if(args.length==1){
           sliderSeeker.setEnabled(true);
           botonRetroceder10s.setVisible(true);
        } else if (args.length==2){
            titulo = args[1].replaceAll("-", " ");
        } else {
            titulo = args[1].replaceAll("-", " ");
            longitud = Long.valueOf(args[2]);
        }
        this.setSize(600, 400);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        mediaPlayerComponent.getMediaPlayer().setVolume(100);
        
        Pantalla.add(mediaPlayerComponent, BorderLayout.CENTER);

        mediaPlayerComponent.getMediaPlayer().prepareMedia(args[0]);
        mediaPlayerComponent.getMediaPlayer().parseMedia();
        if(args.length==1){
            labelTitulo.setText("<html>"+mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle()+"</html>");
        }else if(args.length>=2){
            labelTitulo.setText(titulo);
        }

        sliderSeeker.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                if (!llamadaProgramada) {
                    valorActualSlider = sliderSeeker.getValue();
                    long valor = (valorActualSlider * longitud) / 100;
                    mediaPlayerComponent.getMediaPlayer().skip(-longitud);
                    if (valor != longitud) {
                        mediaPlayerComponent.getMediaPlayer().skip(valor);
                    } else {
                        sliderSeeker.setValue(0);
                    }
                }
                llamadaProgramada = false;
            }
        });
        
        sliderVolumen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                mediaPlayerComponent.getMediaPlayer().setVolume(sliderVolumen.getValue());
            }
        });
        
        
        //Agregando Listener al Reproductor para cada Estado del reproductor.
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(
                new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (args.length == 1) {
                            setTitle(String.format(
                                    "%s - VODUC",
                                    mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle()
                            ));
                            
                            longitud = mediaPlayerComponent.getMediaPlayer().getLength();

                        } else if (args.length >= 2) {
                            setTitle(String.format(
                                    "%s - VODUC",
                                    titulo.replace("<html><b>", "").replace("</b>", "").replace("</html>", "")
                            ));
                        } 
                    }
                });
                
                
                if(args.length==1 || args.length==3){
                    new Thread() {
                        public void run() {
                            long tiempo = longitud / 100;
                            while (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
                                try {
                                    Thread.sleep(tiempo);
                                    valorActualSlider++;
                                    llamadaProgramada = true;
                                    System.out.println("PRUEBA: ");
                                    sliderSeeker.setValue(valorActualSlider);
                                } catch (InterruptedException ex) {
                                }
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //closeWindow();
                        mediaPlayerComponent.getMediaPlayer().stop();
                    }
                });
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(
                                este,
                                "No se pudo reproducir el archivo.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        closeWindow();
                    }
                });
            }

            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
                if (newCount == 0) {
                    return;
                }
                File logoFile = new File("logo.png");
                if (logoFile.exists()) {
                    Logo logo = Logo.logo()
                            .file("logo.png")
                            .position(libvlc_logo_position_e.top_left)
                            .opacity(0.3f)
                            .enable();
                    mediaPlayerComponent.getMediaPlayer().setLogo(logo);
                    mediaPlayerComponent.getMediaPlayer().enableLogo(true);
                }

            }
        }
        );
        
        mediaPlayerComponent.getMediaPlayer().setFullScreenStrategy(
                new DefaultAdaptiveRuntimeFullScreenStrategy(this){
                    @Override
                    protected void beforeEnterFullScreen() {
                        //ControlesArriba.setVisible(false);
                        //Controles.setVisible(false);
                    }

                    @Override
                    protected void afterExitFullScreen() {
                        //ControlesArriba.setVisible(true);
                        //Controles.setVisible(true);
                    }
                }
        );
        //Marquee
        Marquee marquee = Marquee.marquee()
                .text(String.format("%s", mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle()))
                .location(5,5)
                .size(40)
                .colour(Color.WHITE)
                .timeout(3000)
                .position(libvlc_marquee_position_e.bottom)
                .opacity(0.8f)
                .enable();
        //Aplicando Marquee
        mediaPlayerComponent.getMediaPlayer().setMarquee(marquee);
        mediaPlayerComponent.getMediaPlayer().playMedia(args[0]);
    }
    
    private void closeWindow() {
        this.dispatchEvent(new WindowEvent(este, WindowEvent.WINDOW_CLOSING));
    }
    
    private void fullScreen(boolean wantToGoFull){
        if (wantToGoFull){
            mediaPlayerComponent.getMediaPlayer().setFullScreen(true);
            mediaPlayerComponent.getVideoSurface().requestFocus();
            if(!Controles.isVisible()){
                Controles.setVisible(true);
                Controles.setVisible(true);
            }
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if(Controles.isVisible()){
                            if(panelVolumenVisible){
                                PanelVolumen.setVisible(false);
                                panelVolumenVisible = false;
                            }
                            Controles.setVisible(false);
                            ControlesArriba.setVisible(false);
                            
                        }
                    } catch (InterruptedException v) {
                        System.out.println(v);
                    }
                }
            }.start();
        } else {
            mediaPlayerComponent.getMediaPlayer().setFullScreen(false);
            if(!Controles.isVisible()){
                ControlesArriba.setVisible(true);
                Controles.setVisible(true);
            }
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if(Controles.isVisible()){
                            Controles.setVisible(false);
                            ControlesArriba.setVisible(false);
                        }
                    } catch (InterruptedException v) {
                        System.out.println(v);
                    }
                }
            }.start();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Pantalla = new javax.swing.JPanel();
        ControlesArriba = new javax.swing.JPanel();
        botonAtras = new javax.swing.JButton();
        labelTitulo = new javax.swing.JLabel();
        botonVolumen = new javax.swing.JButton();
        Controles = new javax.swing.JPanel();
        botonPlay = new javax.swing.JButton();
        botonRetroceder10s = new javax.swing.JButton();
        sliderSeeker = new javax.swing.JSlider();
        botonPantallaCompleta = new javax.swing.JButton();
        PanelVolumen = new javax.swing.JPanel();
        sliderVolumen = new javax.swing.JSlider();
        botonMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("VODUC");
        setBounds(new java.awt.Rectangle(100, 100, 600, 400));
        setSize(new java.awt.Dimension(600, 400));

        Pantalla.setBackground(new java.awt.Color(0, 0, 0));
        Pantalla.setMinimumSize(new java.awt.Dimension(600, 400));
        Pantalla.setLayout(new java.awt.BorderLayout());

        ControlesArriba.setBackground(new Color(0,0,0,0));
        ControlesArriba.setOpaque(false);
        ControlesArriba.setLayout(new javax.swing.BoxLayout(ControlesArriba, javax.swing.BoxLayout.LINE_AXIS));

        botonAtras.setBackground(new Color(0,0,0,0));
        botonAtras.setForeground(new java.awt.Color(255, 255, 255));
        botonAtras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/backarrow.png"))); // NOI18N
        botonAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAtrasActionPerformed(evt);
            }
        });
        ControlesArriba.add(botonAtras);

        labelTitulo.setBackground(new Color(0,0,0,0));
        labelTitulo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(255, 255, 255));
        labelTitulo.setText("<html><strong><b>Título</b></strong> T7, E1</html>");
        ControlesArriba.add(labelTitulo);

        botonVolumen.setBackground(new Color(0,0,0,0));
        botonVolumen.setForeground(new java.awt.Color(255, 255, 255));
        botonVolumen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/audio.png"))); // NOI18N
        botonVolumen.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        botonVolumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolumenActionPerformed(evt);
            }
        });
        ControlesArriba.add(botonVolumen);

        Pantalla.add(ControlesArriba, java.awt.BorderLayout.NORTH);

        Controles.setBackground(new Color(0,0,0,0));
        Controles.setOpaque(false);
        Controles.setLayout(new javax.swing.BoxLayout(Controles, javax.swing.BoxLayout.LINE_AXIS));

        botonPlay.setBackground(new Color(0,0,0,0));
        botonPlay.setForeground(new java.awt.Color(255, 255, 255));
        botonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause.png"))); // NOI18N
        botonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPlayActionPerformed(evt);
            }
        });
        Controles.add(botonPlay);

        botonRetroceder10s.setBackground(new Color(0,0,0,0));
        botonRetroceder10s.setForeground(new java.awt.Color(255, 255, 255));
        botonRetroceder10s.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reward.png"))); // NOI18N
        botonRetroceder10s.setVisible(false);
        botonRetroceder10s.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRetroceder10sActionPerformed(evt);
            }
        });
        Controles.add(botonRetroceder10s);

        sliderSeeker.setValue(0);
        sliderSeeker.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sliderSeeker.setEnabled(false);
        Controles.add(sliderSeeker);

        botonPantallaCompleta.setBackground(new Color(0,0,0,0));
        botonPantallaCompleta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fullscreen.png"))); // NOI18N
        botonPantallaCompleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPantallaCompletaActionPerformed(evt);
            }
        });
        Controles.add(botonPantallaCompleta);

        Pantalla.add(Controles, java.awt.BorderLayout.SOUTH);
        Controles.getAccessibleContext().setAccessibleName("");

        PanelVolumen.setBackground(new Color(0,0,0,0)
        );
        PanelVolumen.setOpaque(false);

        sliderVolumen.setOrientation(javax.swing.JSlider.VERTICAL);
        sliderVolumen.setValue(100);

        botonMute.setBackground(new Color(0,0,0,0));
        botonMute.setForeground(new java.awt.Color(255, 255, 255));
        botonMute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/noaudio.png"))); // NOI18N
        botonMute.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        botonMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonMuteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelVolumenLayout = new javax.swing.GroupLayout(PanelVolumen);
        PanelVolumen.setLayout(PanelVolumenLayout);
        PanelVolumenLayout.setHorizontalGroup(
            PanelVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sliderVolumen, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(botonMute)
        );
        PanelVolumenLayout.setVerticalGroup(
            PanelVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVolumenLayout.createSequentialGroup()
                .addComponent(sliderVolumen, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonMute)
                .addContainerGap())
        );

        PanelVolumen.setVisible(false);

        Pantalla.add(PanelVolumen, java.awt.BorderLayout.LINE_END);

        getContentPane().add(Pantalla, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonRetroceder10sActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRetroceder10sActionPerformed
        // TODO add your handling code here:
        mediaPlayerComponent.getMediaPlayer().skip(10000);
    }//GEN-LAST:event_botonRetroceder10sActionPerformed

    private void botonVolumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolumenActionPerformed
        // TODO add your handling code here:
        if(panelVolumenVisible){
            PanelVolumen.setVisible(false);
            panelVolumenVisible = false;
        } else {
            PanelVolumen.setVisible(true);
            panelVolumenVisible = true;
        }
        
    }//GEN-LAST:event_botonVolumenActionPerformed

    private void botonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPlayActionPerformed
        // TODO add your handling code here:
        mediaPlayerComponent.getMediaPlayer().pause();
        if (Reproduciendo) {
            botonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/play.png")));
            Reproduciendo = false;
        } else {
            botonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause.png")));
            Reproduciendo = true;
        }
    }//GEN-LAST:event_botonPlayActionPerformed

    private void botonPantallaCompletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPantallaCompletaActionPerformed
        // TODO add your handling code here:
        if (mediaPlayerComponent.getMediaPlayer().isFullScreen()) {
            fullScreen(false);
        } else {
            fullScreen(true);
        }
    }//GEN-LAST:event_botonPantallaCompletaActionPerformed

    private void botonMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonMuteActionPerformed
        // TODO add your handling code here:
        sliderVolumen.setValue(0);
    }//GEN-LAST:event_botonMuteActionPerformed

    private void botonAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAtrasActionPerformed
        // TODO add your handling code here:
        closeWindow();
    }//GEN-LAST:event_botonAtrasActionPerformed

    /**
     * @param args the command line arguments
     */
    
    /*public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaReproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaReproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaReproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaReproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaReproductor(args).setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Controles;
    private javax.swing.JPanel ControlesArriba;
    private javax.swing.JPanel PanelVolumen;
    private javax.swing.JPanel Pantalla;
    private javax.swing.JButton botonAtras;
    private javax.swing.JButton botonMute;
    private javax.swing.JButton botonPantallaCompleta;
    private javax.swing.JButton botonPlay;
    private javax.swing.JButton botonRetroceder10s;
    private javax.swing.JButton botonVolumen;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JSlider sliderSeeker;
    private javax.swing.JSlider sliderVolumen;
    // End of variables declaration//GEN-END:variables
}
