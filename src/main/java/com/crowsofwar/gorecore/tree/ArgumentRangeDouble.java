/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.tree;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ArgumentRangeDouble implements IArgument<Double> {
	
	private final double defaultValue;
	private final boolean optional;
	private final double min;
	private final double max;
	private final String name;
	
	public ArgumentRangeDouble(String name, double min, double max) {
		this.name = name;
		this.defaultValue = 0;
		this.optional = false;
		this.min = min;
		this.max = max;
	}
	
	public ArgumentRangeDouble(String name, double min, double max, double defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.optional = true;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public boolean isOptional() {
		return optional;
	}
	
	@Override
	public Double getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public Double convert(String input) {
		double value = ITypeConverter.CONVERTER_DOUBLE.convert(input);
		if (value < min || value > max) {
			throw new TreeCommandException("gc.tree.error.rangeDouble", name, min, max);
		}
		return value;
	}
	
	@Override
	public String getArgumentName() {
		return name;
	}
	
	@Override
	public String getHelpString() {
		char open = isOptional() ? '[' : '<';
		char close = isOptional() ? ']' : '>';
		return open + "any number " + min + "-" + max + close;
	}
	
	@Override
	public String getSpecificationString() {
		char open = isOptional() ? '[' : '<';
		char close = isOptional() ? ']' : '>';
		return open + name + close;
	}
	
	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput) {
		return new ArrayList<>();
	}
	
}
