/*
*	This file is part of STRATEX.
*
*    STRATEX is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRATEX is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRATEX.  If not, see <http://www.gnu.org/licenses/>.
*/

package twitter.provider.parameter.streamingAPI;

import junit.framework.Assert;
import org.junit.Test;
import stratex.twitter.provider.parameter.streamingAPI.Follow;

public class FollowTest {



    @Test
    public void testGetParameterValue() throws Exception {
        long[] aux = new long[] { 1,2,3};
        Follow follow = new Follow(aux);
        aux[0] = 20;
        // Validates that array was cloned and not referenced
        Assert.assertFalse(follow.getParameterValue().contains("20"));
        // Validates that we get something out of the parameter
        Assert.assertNotNull(follow.getParameterValue());
        // Validate the returned parameter data for the specified input value
        Assert.assertEquals("1,2,3", follow.getParameterValue());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testThrowsIllegalArgumentExceptionWhenLengthOf5000IsExceeded() throws IllegalArgumentException
    {
        long[] aux = new long[5001];
        for(int i = 0; i < 5001; ++i)
        {
            aux[i] = i;
        }
         Follow follow = new Follow(aux);
    }

}
