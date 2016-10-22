package asciindex.model.indexing;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class ChapterTitle {
	private int pos;
	private String text;


	public ChapterTitle() {
	}

	public ChapterTitle(int pos, String text) {
		this.pos = pos;
		this.text = text;
	}

	public String text() {
		return text;
	}

	public int getPos() {
		return pos;
	}

	public String getText() {
		return text;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setText(String text) {
		this.text = text;
	}
}
