/*
 * Copyright (C) 2015 Alexander Christian <alex(at)root1.de>. All rights reserved.
 * 
 * This file is part of slicKnx.
 *
 *   slicKnx is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   slicKnx is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with slicKnx.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.root1.slicknx;

/**
 *
 * @author achristian
 */
public interface GroupAddressListener {
    
    /**
     * Triggered whenever a matching GA is read from someone
     * @param event 
     */
    public void readRequest(GroupAddressEvent event);
    /**
     * Triggered whenever a matching GA get's an answer to a preceding read request
     * @param event 
     */
    public void readResponse(GroupAddressEvent event);
    
    /**
     * Triggered whenever a matching HA is written by someone
     * @param event 
     */
    public void write(GroupAddressEvent event);
    
    
}
