package dna.alignment;

public class NeedlemanWunsch {
	
//	public NeedlemanWunsch(String seqA, String seqB) {
//		a = seqA;
//		b = seqB;
//	}
	
	public static void main(String[] args) {
//        double identity = align("MKNLASREVNIYVNGKLV", "QMASREVNIYVNGKL");
		double identity = align("SGETSYTNSFKVKVLKNGFLFIPHLPASYIIDNDLYQRIYKIA", "SGETSYTNSFKVMVKSDGFLFIPRMPASYPLDNDLYQRIYKIA");
        System.out.println("identity = " + identity);
    }

    public static double align(String a, String b) {
    	int match = 1;
    	int mismatch = -1;
    	int gap = -2 ;
    	
        int[][] T = new int[a.length() + 1][b.length() + 1];
        String[][] win = new String[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++)
            T[i][0] = -i;

        for (int i = 0; i <= b.length(); i++)
            T[0][i] = -i;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
            	int Tab=Integer.MIN_VALUE, Tb=Integer.MIN_VALUE;
            	int Ta=Integer.MIN_VALUE, max=Integer.MIN_VALUE;
            	if (a.charAt(i - 1) == b.charAt(j - 1))
            		Tab = T[i-1][j-1] + match;
            	else
            		Tab = T[i-1][j-1] + mismatch;
            	Ta = T[i-1][j] + gap;
            	Tb = T[i][j-1] + gap;
            	
            	if(Ta > Tb) {
            		win[i][j] = "a";
            	} else if(Ta < Tb) {
            		win[i][j] = "b";
            	} else {
            		win[i][j] = "a,b";
            	}
            	max = Math.max(Ta,Tb);
            	if(Tab > max) {
            		win[i][j] = "ab";
            		max = Math.max(Tab,max);
            	} else if ( Tab == max){
            		win[i][j] += ",ab";
            	}
            	
            	T[i][j] = max;
            }
        }

        StringBuilder aa = new StringBuilder(), bb = new StringBuilder();
        int countT = 0;
        int countF = 0;
        for (int i = a.length(), j = b.length(); i > 0 && j > 0; ) {
        	String[] direction = win[i][j].split(",");
        	String dd = "";
        	if(direction.length > 1) {
        		int max = Integer.MIN_VALUE;
        		
        		for(String d: direction) {
        			int tmpMax;
        			String tmpdd;
            		if(d.equals("ab")) {
            			tmpMax = T[i-1][j-1];
            			tmpdd = "ab";
            		} else if(d.equals("a")) {
            			tmpMax = T[i-1][j];
            			tmpdd = "a";
            		} else {
            			tmpMax = T[i][j-1];
            			tmpdd = "b";
            		}
            		if(tmpMax > max)
            			dd = tmpdd;
            	}
        	} else {
        		dd = direction[0];
        	}
        	
        	if(dd.equals("ab")) {
    			aa.append(a.charAt(--i));
            	bb.append(b.charAt(--j));
            	if(a.charAt(i) == b.charAt(j))
            		countT++;
            	else
            		countF++;
    		} else if(dd.equals("a")) {
    			aa.append(a.charAt(--i));
            	bb.append("-");
            	countF++;
    		} else {
    			aa.append("-");
            	bb.append(b.charAt(--i));
            	countF++;
    		}
        	
        }
//        System.out.println(aa.reverse().toString());
//        System.out.println(bb.reverse().toString());

        return (double)countT /  (double)(countT + countF);
    }
}