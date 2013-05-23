package com.slamdunk.quester.utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MessageBox extends Window {
        private MessageBoxStyle style;
        private Label labelQuestion;
        private TextButton buttonYes;
        private TextButton buttonNo;
        
        public MessageBox(String title, String question, BitmapFont textFont, Skin skin)        {               
                super(title, skin);

                labelQuestion = new Label(question, new Label.LabelStyle(textFont, Color.WHITE));

                buttonYes = new TextButton("yes", skin);                
                buttonYes.setWidth(buttonYes.getWidth()*2.5f);
                buttonYes.setHeight(buttonYes.getHeight()*1.5f);
                
                buttonNo = new TextButton("no", skin);
                buttonNo.setX(2*buttonYes.getWidth());
                buttonNo.setWidth(buttonYes.getWidth());
                buttonNo.setHeight(buttonYes.getHeight());

                buttonNo.addListener(new ClickListener() {                                              
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                                setVisible(false);
                        }
                });
                
                buttonYes.addListener(new ClickListener() {                                             
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                                setVisible(false);
                        }
                });
                
                labelQuestion.setX(getPadLeft());
                labelQuestion.setY(1.3f*buttonYes.getHeight());

                setWidth(labelQuestion.getWidth()+getPadLeft()+getPadRight());
                setHeight(labelQuestion.getHeight()+buttonNo.getHeight()+getPadTop()+getPadBottom());
                
                buttonYes.translate(getWidth()/2-1.5f*buttonYes.getWidth(), 0);
                buttonNo.translate(getWidth()/2-1.5f*buttonYes.getWidth(), 0);
                
                buttonYes.translate(0, getPadBottom());
                buttonNo.translate(0, getPadBottom());
                

                addActor(labelQuestion);
                addActor(buttonYes);
                addActor(buttonNo);

                setVisible(false);
        }       

        public void setStyle (MessageBoxStyle style) {
                if (style == null) throw new IllegalArgumentException("style cannot be null.");
                this.style = style;
                //this.getStyle().titleFont = style.font;
                //this.getStyle().titleFontColor = style.fontColor;
                labelQuestion.getStyle().font = style.font;
                labelQuestion.setColor(style.fontColor);
                invalidateHierarchy();
        }
        
        @Override
        public void draw (SpriteBatch batch, float parentAlpha) {
                if(isVisible())
                {
                        super.draw(batch, parentAlpha);
                }                       
        }                               

        public void show()
        {
                setVisible(true);
        }
        
        public void hide()
        {
                setVisible(false);
        }

        public void addPositiveListener(EventListener listener) {
                buttonYes.addListener(listener);
        }
        
        public void addNegativeListener(EventListener listener) {
                buttonNo.addListener(listener);
        }
        
        static public class MessageBoxStyle {
                public BitmapFont font;
                
                /** Optional. */
                public Color fontColor;         

                public MessageBoxStyle () {
                }

                public MessageBoxStyle (BitmapFont font, Color fontColor) {
                        this.font = font;
                        this.fontColor = fontColor;
                }

                public MessageBoxStyle (MessageBoxStyle style) {
                        this.font = style.font;
                        if (style.fontColor != null) this.fontColor = new Color(style.fontColor);
                }
        }
}