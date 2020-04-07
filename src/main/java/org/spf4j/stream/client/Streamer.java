
package org.spf4j.stream.client;

import java.awt.event.ItemEvent;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/**
 *
 * @author Zoltan Farkas
 */
public class Streamer extends javax.swing.JFrame {

  private DefaultCameraStreamer streamer;

  /**
   * Creates new form Streamer
   */
  public Streamer() {
    initComponents();
  }
  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
   * content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jToolBar1 = new javax.swing.JToolBar();
    uploadUrl = new javax.swing.JTextField();
    resolution = new javax.swing.JComboBox<>();
    filterSelection = new javax.swing.JComboBox<>();
    playStopButton = new javax.swing.JToggleButton();
    playPanel = new javax.swing.JPanel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setMaximumSize(new java.awt.Dimension(640, 480));
    setMinimumSize(new java.awt.Dimension(640, 480));
    setPreferredSize(new java.awt.Dimension(320, 240));

    jToolBar1.setRollover(true);

    uploadUrl.setText("https://demo.spf4j.org/video/cam/z.m3u8");
    uploadUrl.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        uploadUrlPropertyChange(evt);
      }
    });
    jToolBar1.add(uploadUrl);

    resolution.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Small", "Medium", "Large" }));
    resolution.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        resolutionActionPerformed(evt);
      }
    });
    resolution.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        resolutionPropertyChange(evt);
      }
    });
    jToolBar1.add(resolution);

    filterSelection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CannyEdge", "None" }));
    filterSelection.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        filterSelectionActionPerformed(evt);
      }
    });
    jToolBar1.add(filterSelection);

    playStopButton.setText("Play/Stop");
    playStopButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    playStopButton.setFocusable(false);
    playStopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    playStopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    playStopButton.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        playStopButtonItemStateChanged(evt);
      }
    });
    playStopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        playStopButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(playStopButton);

    javax.swing.GroupLayout playPanelLayout = new javax.swing.GroupLayout(playPanel);
    playPanel.setLayout(playPanelLayout);
    playPanelLayout.setHorizontalGroup(
      playPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    playPanelLayout.setVerticalGroup(
      playPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 431, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(playPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
          .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(playPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void filterSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterSelectionActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_filterSelectionActionPerformed

  private void uploadUrlPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_uploadUrlPropertyChange

  }//GEN-LAST:event_uploadUrlPropertyChange

  private void playStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playStopButtonActionPerformed

  }//GEN-LAST:event_playStopButtonActionPerformed

  private void playStopButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_playStopButtonItemStateChanged
    int stateChange = evt.getStateChange();
    if (stateChange == ItemEvent.SELECTED) {
      try {
        if (this.streamer != null) {
          this.streamer.close();
        }
        this.streamer = new DefaultCameraStreamer(this.playPanel, this.uploadUrl.getText(),
                Resolution.valueOf(resolution.getSelectedItem().toString()));
        this.streamer.play();
      } catch (IOException | InterruptedException ex) {
       throw new RuntimeException(ex);
      }
    } else if (stateChange == ItemEvent.DESELECTED) {
        if (this.streamer != null) {
          try {
            this.streamer.close();
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        }
        this.streamer = null;
    }
  }//GEN-LAST:event_playStopButtonItemStateChanged

  private void resolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resolutionActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_resolutionActionPerformed

  private void resolutionPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_resolutionPropertyChange
    // TODO add your handling code here:
  }//GEN-LAST:event_resolutionPropertyChange


  public JPanel getPlayPanel() {
    return playPanel;
  }

  public JToggleButton getPlayStopButton() {
    return playStopButton;
  }

  public JTextField getUploadUrl() {
    return uploadUrl;
  }



  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox<String> filterSelection;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JPanel playPanel;
  private javax.swing.JToggleButton playStopButton;
  private javax.swing.JComboBox<String> resolution;
  private javax.swing.JTextField uploadUrl;
  // End of variables declaration//GEN-END:variables
}
