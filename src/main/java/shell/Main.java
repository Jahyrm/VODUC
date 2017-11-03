/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shell;

import javax.swing.JFrame;
/**
 *
 * @author Jahyr
 */
public class Main {
   
    /*
    public static String nombre;
    public static Scanner ingresoTexto = new Scanner (System.in);
    public static Scanner ingresoEntero = new Scanner (System.in);
    
    private static String serverAddress = "192.168.1.101";
    private static int serverPort = 3232;
    
    private static RMIInterface servidorObj;
    */
    
    
    //Removí: throws MalformedURLException
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }
        
        
        /*
        System.out.println("Bienvenido a VODUC.");
        System.out.print("Ingrese su nombre: ");
        nombre = ingresoTexto.nextLine();
        System.out.println("Hola "+nombre+", estas son las películas actuales:");
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress, serverPort);
            servidorObj = (RMIInterface) registry.lookup("operacionservidor");
            System.out.println(servidorObj.imprimirLista());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        menu();
        
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
                String vector[] = new String[2];
                vector[0] = "myvideo.mp4";
                vector[1] = "Nada";
        
                // Create and display the form
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new VentanaReproductor(vector).setVisible(true);
                    }
                });
    }
    
    public static void menu(){
        System.out.println("1. Obtener información de película.");
        System.out.println("2. Reproducir película.");
        System.out.println("3. Salir.");
        int opcion = ingresoEntero.nextInt();
        switch(opcion){
            case 1:
                informacionDePelicula();
                break;
            case 2:
                reproducirPelicula();
                break;
            case 3:
                System.out.println("Gracias. Usted ha salido correctamente.");
        }
    }
    
    public static void informacionDePelicula(){
        System.out.print("Ingrese el código de la película que desea obtener información:");
        String codigo = ingresoTexto.nextLine();
        String[] codigos = codigo.split("-");
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress, serverPort);
            servidorObj = (RMIInterface) (registry.lookup("operacionservidor"));
            String infoPeli = servidorObj.solicitarInfoPelicula(codigos[0], codigos[1]);
            if(infoPeli.equals("Código incorrecto.")){
                System.err.println("Error: Código incorrecto.");
            } else {
                System.out.println(infoPeli);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void reproducirPelicula() {
        System.out.print("Ingrese el código de la película que desea obtener información:");
        String codigo = ingresoTexto.nextLine();
        String[] codigos = codigo.split("-");
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress, serverPort);
            servidorObj = (RMIInterface) (registry.lookup("operacionservidor"));
            String args[] = new String[2];
            args[0] = servidorObj.play(codigos[0], codigos[1]);
            if(args[0].equals("Código incorrecto.")){
                System.err.println("Error: Código incorrecto.");
            } else {
                args[1] = "<html><b>"+servidorObj.getTitulo(codigos[0], codigos[1]).replace(" ", "-")+"</b></html>";
            
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
                // Create and display the form
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new VentanaReproductor(args).setVisible(true);
                    }
                });
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
