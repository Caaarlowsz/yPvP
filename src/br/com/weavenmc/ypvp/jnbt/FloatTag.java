package br.com.weavenmc.ypvp.jnbt;

public final class FloatTag extends Tag {
	private final float value;

	public FloatTag(final String name, final float value) {
		super(name);
		this.value = value;
	}

	@Override
	public Float getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		final String name = this.getName();
		String append = "";
		if (name != null && !name.isEmpty()) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Float" + append + ": " + this.value;
	}
}
