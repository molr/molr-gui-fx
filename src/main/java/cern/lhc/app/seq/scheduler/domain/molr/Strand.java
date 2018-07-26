/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.molr;

import static java.util.Objects.requireNonNull;

/**
 * One 'story line' within a mission. A mission might split up in different strands, of which each of them can have its
 * own state (e.g. running, paused ...). (If you would like to relate this to java, you would probably imagine it as a
 * Thread ;-)
 * 
 * @author kfuchsbe
 */
public class Strand {

    private final long id;
    private final String name;

    public Strand(long id, String name) {
        this.id = id;
        this.name = requireNonNull(name, "name must not be null");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id() ^ (id() >>> 32));
        result = prime * result + ((name() == null) ? 0 : name().hashCode());
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
        Strand other = (Strand) obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Strand [id=" + id() + ", name=" + name() + "]";
    }

    public String name() {
        return name;
    }

    public long id() {
        return id;
    }

}
