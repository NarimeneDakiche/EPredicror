/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnapshotsPrep;

/**
 *
 * @author ado_k
 */
class LongRange {

    long min;
    long max;

    public LongRange(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    boolean contains(long l) {
        return (l <= this.max && l >= this.min);
    }

    boolean containsStrict(long l) {
        return (l < this.max && l > this.min);
    }

}
