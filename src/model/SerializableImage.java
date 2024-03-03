package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

//JavaFX images are not serializable by default
//this class wraps image objects and makes them serializable
public class SerializableImage implements Serializable {
    private transient Image image; //transient keyword to exclude from default serialization

    public SerializableImage(Image image) {
        this.image = image;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, "png", byteStream);
        byte[] imageBytes = byteStream.toByteArray();
        out.defaultWriteObject();
        out.writeInt(imageBytes.length);
        out.write(imageBytes);
    }

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, EOFException {
		in.defaultReadObject();
		int length = in.readInt();
		byte[] imageBytes = new byte[length];
		in.readFully(imageBytes);
		ByteArrayInputStream byteStream = new ByteArrayInputStream(imageBytes);
		BufferedImage bufferedImage = ImageIO.read(byteStream);
		image = SwingFXUtils.toFXImage(bufferedImage, null);
	}

    public Image getImage() {
        return image;
    }

	@Override
	public int hashCode() {
		return Objects.hash(image);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializableImage other = (SerializableImage) obj;
		return Objects.equals(image, other.image);
	}
}

