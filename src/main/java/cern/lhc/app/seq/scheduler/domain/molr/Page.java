/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

public class Page {

    private final long number;

    public Page(long number) {
        this.number = number;
    }

    public long number() {
        return number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (number ^ (number >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Page other = (Page) obj;
        if (number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Page [number=" + number + "]";
    }

}
