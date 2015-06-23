/*
 *  Copyright (C) 2015, Jose Manuel Balbás Muriel <jmbalbas87@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jmb.afip.barcode;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 * AFIP Barcode Factory class
 * 
 * @author Jose Manuel Balbás Muriel
 * @version 1.0
 */
public class AFIPBarcodeFactory {
	
	// Cadena a traducir a Interleaved 2 of 5
	private String cadena;
	
	// Anchos para las barras
	final static private boolean[][] anchos = new boolean[][] {
		new boolean[] { false, false, true, true, false}, // 0
		new boolean[] { true, false, false, false, true}, // 1
		new boolean[] { false, true, false, false, true}, // 2
		new boolean[] { true, true, false, false, false}, // 3
		new boolean[] { false, false, true, false, true}, // 4
		new boolean[] { true, false, true, false, false}, // 5
		new boolean[] { false, true, true, false, false}, // 6
		new boolean[] { false, false, false, true, true}, // 7
		new boolean[] { true, false, false, true, false}, // 8
		new boolean[] { false, true, false, true, false}, // 9
	};
	
	// La fuente usada para el texto que va debajo del código
	private Font fuente;
	
	// El alto del código de barras
	private int altoBarras;
	
	// Para pintar o no la cadena
	private boolean pintarCadena;
	
	/**
	 * Constructor
	 * 
	 * @param cadena
	 */
	public AFIPBarcodeFactory(String cadena) {
		setBarcodeString(cadena);
		// Por defecto:
		fuente = null;
		altoBarras = 45;
		pintarCadena = false;
	}
	
	/**
	 * Setter setBarcodeString
	 * 
	 * @param cadena
	 */
	public void setBarcodeString(String cadena) {
		this.cadena = cadena;
	}
	
	/**
	 * Getter getBarcodeString
	 * 
	 * @return cadena
	 */
	public String getBarcodeString() {
		return cadena;
	}
	
	/**
	 * Setter setFont
	 * 
	 * @param fuente
	 * @param tipo
	 * @param tamaño
	 */
	public void setFont(Font fuente) {
		this.fuente = fuente;
	}
	
	/**
	 * Setter setBarHeight
	 * 
	 * @param altoBarras
	 */
	public void setBarHeight(int altoBarras) {
		this.altoBarras = altoBarras;
	}
	
	/**
	 * Getter getBarHeight
	 * 
	 * @return
	 */
	public int getBarHeight() {
		return altoBarras;
	}
	
	/**
	 * Setter setDrawingText
	 * 
	 * @param pintarCadena
	 */
	public void setDrawingText(boolean pintarCadena) {
		this.pintarCadena = pintarCadena;	
	}
	
	/**
	 * Getter getDrawingText
	 * 
	 * @return
	 */
	public boolean getDrawingText() {
		return pintarCadena;
	}
	
	/**
	 * Devuelve un BufferedImage con la imagen del código de barras
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage createInt2of5(int width, int height) {
		// Creamos un BufferedImage con los parámetros recibidos
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		// Creamos objeto gráfico para dibujar el código de barras
		Graphics2D g = i.createGraphics();

		// Establecemos el color de fondo	
		g.setBackground(Color.WHITE);
		// Establecemos posición y medidas
		g.clearRect(0, 0, width, height);
		
		// Dibujamos el código de barras
		drawInterleaved2of5Barcode(g);
		return i;
	}
		
	/**
	 * Método privado para dibujar el código de barras en formato Interleaved 2 de 5
	 * 
	 * @param g
	 */
	private void drawInterleaved2of5Barcode(Graphics2D g) {
		final GeneralPath path = new GeneralPath();
		
		class LineSegment {
			float x = 0f;
			
			void bar(float ancho) {
				path.moveTo(x,0);
				path.lineTo(x, altoBarras);
				x += ancho;
				path.lineTo(x, altoBarras);
				path.lineTo(x, 0);
				path.closePath();				
			}
			
			void space(float ancho) {
				x+=ancho;
			}
		}
		
		// Tamaño de los segmentos
		LineSegment ls = new LineSegment();
		final float barraFina = 2f;
		final float barraGruesa = barraFina*2f;
		// Color de las barras
		g.setColor(Color.BLACK);
		
		// Secuencia de comienzo
		for (int i=0; i<2; i++) {
			ls.bar(barraFina);
			ls.space(barraFina);
		}
		
		int l = cadena.length();
		for (int i=0 ; i < l-1 ; i++) {
			while(Character.isSpaceChar(cadena.charAt(i)))
				i++;
			int a = cadena.charAt(i)-'0';
			i++;
			while (i<l && Character.isSpaceChar(cadena.charAt(i)))
				i++;
			if (i==l)
				break;
			int b = cadena.charAt(i)-'0';
			
			boolean[] p = anchos[a];
			boolean[] q = anchos[b];
			
			for (int j=0 ; j<5; j++) {
				ls.bar(p[j]?barraGruesa:barraFina);
				ls.space(q[j]?barraGruesa:barraFina);
			}
		}
		
		// Secuencia final
		ls.bar(barraGruesa);
		ls.space(barraFina);
		ls.bar(barraFina);
		
		g.fill(path);
		
		// Pintamos el texto
		if (pintarCadena) {
			if (fuente != null) {
				g.setFont(fuente);
			} else {
				g.setFont(new Font("Arial", Font.PLAIN, 10)); // Por defecto
			}
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontMetrics fontMetrics = g.getFontMetrics();
			g.drawString(cadena, (ls.x - fontMetrics.stringWidth(cadena)) / 2f, altoBarras + fontMetrics.getHeight());
		}
	}
}
