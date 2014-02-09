/*
*	This file is part of SINC.
*
*    SINC is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    SINC is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with SINC.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.sinc.pipeline.engine;

public interface TappedPipeline {
    /**
     * Idea: Opens the pipeline tap, allowing requests to go in and be processed. Probably will put them in a queue
     * @return
     */
    public abstract boolean openTap();

    /**
     * Idea: Closes the tap, not allowing any more requests to be added to the pipeline queue, or just stop processing
     * @return
     */
    public abstract boolean closeTap();

    /**
     * Idea: Closes the tap, not allowing any more requests to be added to the pipeline queue, or just stop processing
     * @return
     */
    public abstract void executePipeline();

}
