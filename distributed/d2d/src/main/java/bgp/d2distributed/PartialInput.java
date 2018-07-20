package bgp.d2distributed;

import org.apache.hadoop.io.Text;

public class PartialInput {
	private Text idS, idQ;
	private Text linesS, linesQ;
	
	public PartialInput(Text idS, Text idQ, Text linesS, Text linesQ) {
		this.idS = idS;
		this.idQ = idQ;
		this.linesS = linesS;
		this.linesQ = linesQ;
	}

	public Text getIdS() {
		return idS;
	}

	public void setIdS(Text idS) {
		this.idS = idS;
	}

	public Text getIdQ() {
		return idQ;
	}

	public void setIdQ(Text idQ) {
		this.idQ = idQ;
	}

	public Text getLinesS() {
		return linesS;
	}

	public void setLinesS(Text linesS) {
		this.linesS = linesS;
	}

	public Text getLinesQ() {
		return linesQ;
	}

	public void setLinesQ(Text linesQ) {
		this.linesQ = linesQ;
	}
	
	
}
