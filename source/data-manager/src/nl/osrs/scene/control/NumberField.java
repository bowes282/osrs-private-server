package nl.osrs.scene.control;

import javafx.scene.control.TextField;

public class NumberField extends TextField {
		private int minValue;
		private int maxValue;

	    @Override
	    public void replaceText(int start, int end, String text) {
	        if (validate(text)) {
	            super.replaceText(start, end, text);
	        }
	    }

	    @Override
	    public void replaceSelection(String text) {
	        if (validate(text)) {
	            super.replaceSelection(text);
	        }
	    }

	    private boolean validate(String text) {
	        if (!text.matches("^-?\\d?$"))
	        	return false;
	        
	        if (text.contains("-") && getText().length() == 0) {
	        	if (minValue > -1)
	        		return false;
        		else
        			return true;
	        }
	        	
	        int nextValue = Integer.parseInt(getText() + text);

	        if (nextValue < minValue) {
	        	setText(minValue);
	        	return false;
	        }
	        
	        if (nextValue > maxValue) {
	        	setText(maxValue);
	        	return false;
	        }
	        
	        return true;
	    }
	    
	    public void setText(int value) {
	    	super.setText(String.valueOf(value));
	    }
	    
	    public int getValue() {
	    	return Integer.parseInt(getText());
	    }

		public int getMinValue() {
			return minValue;
		}

		public void setMinValue(int minValue) {
			this.minValue = minValue;
		}

		public int getMaxValue() {
			return maxValue;
		}

		public void setMaxValue(int maxValue) {
			this.maxValue = maxValue;
		}
	    
		public void setRange(int minValue, int maxValue) {
			this.setMinValue(minValue);
			this.setMaxValue(maxValue);
		}
		
}
