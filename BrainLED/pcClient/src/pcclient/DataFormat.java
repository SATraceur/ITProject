/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcclient;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yeqin
 */
public class DataFormat {
    public int dataID;
    public int blink;
    public int winkLeft;
    public int winkRight;
    public int lookLeft;
    public int lookRight;
    public int smail;
    public int clench;
    public int smirkLeft;
    public int smirkRight;
    public int laugh;
    public int frown;
    public int surprise;
    public int bawl;
    public int sob;

    public DataFormat(int dataID, int blink, int winkLeft, int winkRight, int lookLeft, int lookRight, int smail, int clench, int smirkLeft, int smirkRight, int laugh, int frown, int surprise, int bawl, int sob) {
        this.dataID = dataID;
        this.blink = blink;
        this.winkLeft = winkLeft;
        this.winkRight = winkRight;
        this.lookLeft = lookLeft;
        this.lookRight = lookRight;
        this.smail = smail;
        this.clench = clench;
        this.smirkLeft = smirkLeft;
        this.smirkRight = smirkRight;
        this.laugh = laugh;
        this.frown = frown;
        this.surprise = surprise;
        this.bawl = bawl;
        this.sob = sob;
    }
    
}

