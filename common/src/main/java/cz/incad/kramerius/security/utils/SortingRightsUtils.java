/*
 * Copyright (C) 2010 Pavel Stastny
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.kramerius.security.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cz.incad.kramerius.security.Right;

public class SortingRightsUtils {

    public static Right[] sortRights(Right[] findRights, List<String> uuids) {

        ArrayList<Right> noCriterium = new ArrayList<Right>();
        ArrayList<Right> negativeFixedPriorty = new ArrayList<Right>();
        ArrayList<Right> positiveFixedPriority = new ArrayList<Right>();
        
        ArrayList<Right> dynamicHintMin = new ArrayList<Right>();
        ArrayList<Right> dynamicHintNormal = new ArrayList<Right>();
        ArrayList<Right> dynamicHintMax = new ArrayList<Right>();
        
        ArrayList<Right> processing = new ArrayList<Right>(Arrays.asList(findRights));
        // vyzobani pravidel bez kriterii 
        for (Iterator iterator = processing.iterator(); iterator.hasNext();) {
            Right right = (Right) iterator.next();
            if (right.getCriterium() == null) {
                noCriterium.add(right);
                iterator.remove();
            }
        }
        // vyzobani pravidel s pevne nadefinovanou prioritou
        for (Iterator iterator = processing.iterator(); iterator.hasNext();) {
            Right right = (Right) iterator.next();
            if (right.getCriterium().getFixedPriority() < 0) {
                negativeFixedPriorty.add(right);
                iterator.remove();
            } else if (right.getCriterium().getFixedPriority() > 0) {
                positiveFixedPriority.add(right);
                iterator.remove();
            } 
        }
        
        for (Iterator iterator = processing.iterator(); iterator.hasNext();) {
            Right right = (Right) iterator.next();
            switch(right.getCriterium().getPriorityHint()) {
                case MIN: {
                    dynamicHintMin.add(right);
                    iterator.remove();
                }
                break;
                
                case NORMAL: {
                    dynamicHintNormal.add(right);
                    iterator.remove();
                }
                break;
                
                case MAX: {
                    dynamicHintMax.add(right);
                    iterator.remove();
                }
                break;
            }
        }
        
        SortingRightsUtils.sortByUUID(noCriterium, uuids);
        SortingRightsUtils.sortByFixedPriority(negativeFixedPriorty);
        SortingRightsUtils.sortByFixedPriority(positiveFixedPriority);
        SortingRightsUtils.sortByUUID(dynamicHintMax, uuids);
        SortingRightsUtils.sortByUUID(dynamicHintNormal, uuids);
        SortingRightsUtils.sortByUUID(dynamicHintMin, uuids);
        
        ArrayList<Right> result = new ArrayList<Right>();
        result.addAll(positiveFixedPriority);
        result.addAll(dynamicHintMax);
        result.addAll(dynamicHintNormal);
        result.addAll(dynamicHintMin);
        result.addAll(negativeFixedPriorty);
        result.addAll(noCriterium);
        
        return (Right[]) result.toArray(new Right[result.size()]);
    }

    public static void sortByUUID(final List<Right> list, final List<String>uuids) {
        Collections.sort(list, new Comparator<Right>() {
    
            @Override
            public int compare(Right o1, Right o2) {
                int thisVal = uuids.indexOf(o1.getUUID());
                int anotherVal = uuids.indexOf(o2.getUUID());
                return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
            }
            
        });
    }

    public static void sortByFixedPriority(List<Right> list) {
        Collections.sort(list,  new Comparator<Right>() {
            @Override
            public int compare(Right o1, Right o2) {
                int thisVal = o1.getCriterium().getFixedPriority();
                int anotherVal = o2.getCriterium().getFixedPriority();
                return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
            }
        });
    }

}
