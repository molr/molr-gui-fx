/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

public class Line {

    private final Mission missionId;
    private final Page page;
    private final long number;
    private final String text;

    public Line(Mission missionId, Page page, long number, String text) {
        super();
        this.missionId = missionId;
        this.page = page;
        this.number = number;
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((missionId() == null) ? 0 : missionId().hashCode());
        result = prime * result + (int) (number() ^ (number() >>> 32));
        result = prime * result + ((page() == null) ? 0 : page().hashCode());
        result = prime * result + ((text() == null) ? 0 : text().hashCode());
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
        Line other = (Line) obj;
        if (missionId() == null) {
            if (other.missionId() != null) {
                return false;
            }
        } else if (!missionId().equals(other.missionId())) {
            return false;
        }
        if (number() != other.number()) {
            return false;
        }
        if (page() == null) {
            if (other.page() != null) {
                return false;
            }
        } else if (!page().equals(other.page())) {
            return false;
        }
        if (text() == null) {
            if (other.text() != null) {
                return false;
            }
        } else if (!text().equals(other.text())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Line [missionId=" + missionId() + ", page=" + page() + ", number=" + number() + ", text=" + text()
                + "]";
    }

    public Mission missionId() {
        return missionId;
    }

    public Page page() {
        return page;
    }

    public long number() {
        return number;
    }

    public String text() {
        return text;
    }

}
