/*
 * Copyright © Wynntils 2023-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.text;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class PartStyle {
    private static final String STYLE_PREFIX = "§";
    private static final Int2ObjectMap<ChatFormatting> INTEGER_TO_CHATFORMATTING_MAP = Arrays.stream(
                    ChatFormatting.values())
            .filter(ChatFormatting::isColor)
            .collect(
                    () -> new Int2ObjectOpenHashMap<>(ChatFormatting.values().length),
                    (map, cf) -> map.put(cf.getColor(), cf),
                    Int2ObjectMap::putAll);

    private final StyledTextPart owner;

    private final Style style;

    private PartStyle(StyledTextPart owner, Style style) {
        this.style = style == null ? Style.EMPTY : style;
        this.owner = owner;
    }

    PartStyle(PartStyle partStyle, StyledTextPart owner) {
        this.owner = owner;
        this.style = partStyle.getStyle();
    }

    static PartStyle fromStyle(Style style, StyledTextPart owner, Style parentStyle) {
        Style inheritedStyle;

        if (parentStyle == null) {
            inheritedStyle = style;
        } else {
            // This changes properties that are null, as-in, inherting from the previous style.
            inheritedStyle = style.applyTo(parentStyle);
        }

        return new PartStyle(owner, inheritedStyle);
    }

    public String asString(PartStyle previousPartStyle, StyleType type) {
        // Rules of converting a Style to a String:
        // Every style is prefixed with a §.
        // 0. Every style string is fully qualified, meaning that it contains all the formatting, and reset if needed.
        // 1. Style color is converted to a color segment.
        //    A color segment is the prefix and the chatFormatting char.
        //    If this is a custom color, a hex color code is used.
        //    Example: §#FF0000 or §1
        // 2. Formatting is converted the same way as in the Style class.
        // 3. Click events are wrapped in square brackets, and is represented as an id.
        //    The parent of this style's owner is responsible for keeping track of click events.
        //    Example: §[1] -> (1st click event)
        // 4. Hover events are wrapped in angle brackets, and is represented as an id.
        //    The parent of this style's owner is responsible for keeping track of hover events.
        //    Example: §<1> -> (1st hover event)

        if (type == StyleType.NONE) return "";

        StringBuilder styleString = new StringBuilder();

        boolean skipFormatting = false;

        // If the color is the same as the previous style, we can try to construct a difference.
        // If colors don't match, the inserted color will reset the formatting, thus we need to include all formatting.
        // If the current color is NONE, we NEED to try to construct a difference,
        // since there will be no color formatting resetting the formatting afterwards.

        if (previousPartStyle != null
                && (this.style.getColor() == null
                        || Objects.equals(
                                this.style.getColor(),
                                previousPartStyle.getStyle().getColor()))) {
            String differenceString = this.tryConstructDifference(previousPartStyle, type == StyleType.INCLUDE_EVENTS);

            if (differenceString != null) {
                styleString.append(differenceString);
                skipFormatting = true;
            } else {
                styleString.append(STYLE_PREFIX).append(ChatFormatting.RESET.getChar());
            }
        }

        if (!skipFormatting) {
            // 1. Color
            if (this.style.getColor() != null) {
                ChatFormatting chatFormatting = INTEGER_TO_CHATFORMATTING_MAP.get(
                        this.getStyle().getColor().getValue());

                if (chatFormatting != null) {
                    styleString.append(STYLE_PREFIX).append(chatFormatting.getChar());
                } else {
                    styleString
                            .append(STYLE_PREFIX)
                            .append(this.getStyle().getColor().toString());
                }
            }

            // 2. Formatting
            if (this.style.isBold()) {
                styleString.append(STYLE_PREFIX).append(ChatFormatting.BOLD.getChar());
            }
            if (this.style.isItalic()) {
                styleString.append(STYLE_PREFIX).append(ChatFormatting.ITALIC.getChar());
            }
            if (this.style.isStrikethrough()) {
                styleString.append(STYLE_PREFIX).append(ChatFormatting.STRIKETHROUGH.getChar());
            }
            if (this.style.isUnderlined()) {
                styleString.append(STYLE_PREFIX).append(ChatFormatting.UNDERLINE.getChar());
            }
            if (this.style.isObfuscated()) {
                styleString.append(STYLE_PREFIX).append(ChatFormatting.OBFUSCATED.getChar());
            }

            if (type == StyleType.INCLUDE_EVENTS) {
                // 3. Click event
                if (this.style.getClickEvent() != null) {
                    styleString
                            .append(STYLE_PREFIX)
                            .append("[")
                            .append(owner.getParent().getClickEventIndex(this.style.getClickEvent()))
                            .append("]");
                }

                // 4. Hover event
                if (this.style.getHoverEvent() != null) {
                    styleString
                            .append(STYLE_PREFIX)
                            .append("<")
                            .append(owner.getParent().getHoverEventIndex(this.style.getHoverEvent()))
                            .append(">");
                }
            }
        }

        return styleString.toString();
    }

    public Style getStyle() {
        return this.style;
    }

    public PartStyle withColor(ChatFormatting color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("ChatFormatting " + color + " is not a color!");
        }
        TextColor textColor = TextColor.fromLegacyFormat(color);
        if (Objects.equals(this.style.getColor(), textColor)) return this;
        return new PartStyle(owner, this.style.withColor(textColor));
    }

    public PartStyle withBold(boolean bold) {
        if (Objects.equals(this.style.isBold(), bold)) return this;
        return new PartStyle(owner, this.style.withBold(bold));
    }

    public PartStyle withObfuscated(boolean obfuscated) {
        if (Objects.equals(this.style.isObfuscated(), obfuscated)) return this;
        return new PartStyle(owner, this.style.withObfuscated(obfuscated));
    }

    public PartStyle withStrikethrough(boolean strikethrough) {
        if (Objects.equals(this.style.isStrikethrough(), strikethrough)) return this;
        return new PartStyle(owner, this.style.withStrikethrough(strikethrough));
    }

    public PartStyle withUnderlined(boolean underlined) {
        if (Objects.equals(this.style.isUnderlined(), underlined)) return this;
        return new PartStyle(owner, this.style.withUnderlined(underlined));
    }

    public PartStyle withItalic(boolean italic) {
        if (Objects.equals(this.style.isItalic(), italic)) return this;
        return new PartStyle(owner, this.style.withItalic(italic));
    }

    public PartStyle withClickEvent(ClickEvent clickEvent) {
        if (Objects.equals(this.style.getClickEvent(), clickEvent)) return this;
        return new PartStyle(owner, this.style.withClickEvent(clickEvent));
    }

    public PartStyle withHoverEvent(HoverEvent hoverEvent) {
        if (Objects.equals(this.style.getHoverEvent(), hoverEvent)) return this;
        return new PartStyle(owner, this.style.withHoverEvent(hoverEvent));
    }

    private String tryConstructDifference(PartStyle oldPartStyle, boolean includeEvents) {
        StringBuilder add = new StringBuilder();

        Style oldStyle = oldPartStyle.getStyle();
        Style newStyle = this.getStyle();

        if (oldStyle.getColor() == null) {
            if (newStyle.getColor() != null) {
                Arrays.stream(ChatFormatting.values())
                        .filter(c -> c.isColor() && newStyle.getColor().getValue() == c.getColor())
                        .findFirst()
                        .ifPresent(add::append);
            }
        } else if (!Objects.equals(oldStyle.getColor(), newStyle.getColor())) {
            return null;
        }

        if (oldStyle.isObfuscated() != newStyle.isObfuscated()) {
            if (oldStyle.isObfuscated()) return null;
            add.append(ChatFormatting.OBFUSCATED);
        }

        if (oldStyle.isBold() != newStyle.isBold()) {
            if (oldStyle.isBold()) return null;
            add.append(ChatFormatting.BOLD);
        }

        if (oldStyle.isStrikethrough() != newStyle.isStrikethrough()) {
            if (oldStyle.isStrikethrough()) return null;
            add.append(ChatFormatting.STRIKETHROUGH);
        }

        if (oldStyle.isUnderlined() != newStyle.isUnderlined()) {
            if (oldStyle.isUnderlined()) return null;
            add.append(ChatFormatting.UNDERLINE);
        }

        if (oldStyle.isItalic() != newStyle.isItalic()) {
            if (oldStyle.isItalic()) return null;
            add.append(ChatFormatting.ITALIC);
        }

        if (includeEvents) {
            // If there is a click event in the old style, but not in the new one, we can't construct a difference.
            // Otherwise, if the old style and the new style has different events, add the new event.
            // This can happen in two cases:
            // - The old style has an event, but the new one has one as well.
            // - The old style doesn't have an event, but the new does.

            if (oldStyle.getClickEvent() != null && newStyle.getClickEvent() == null) return null;
            if (oldStyle.getClickEvent() != newStyle.getClickEvent()) {
                add.append(STYLE_PREFIX)
                        .append("[")
                        .append(owner.getParent().getClickEventIndex(newStyle.getClickEvent()))
                        .append("]");
            }

            if (oldStyle.getHoverEvent() != null && newStyle.getHoverEvent() == null) return null;
            if (oldStyle.getHoverEvent() != newStyle.getHoverEvent()) {
                add.append(STYLE_PREFIX)
                        .append("<")
                        .append(owner.getParent().getHoverEventIndex(newStyle.getHoverEvent()))
                        .append(">");
            }
        }

        return add.toString();
    }

    @Override
    public String toString() {
        return "PartStyle{" + "color="
                + this.style.getColor() + ", bold="
                + this.style.isBold() + ", italic="
                + this.style.isItalic() + ", underlined="
                + this.style.isUnderlined() + ", strikethrough="
                + this.style.isStrikethrough() + ", obfuscated="
                + this.style.isObfuscated() + ", clickEvent="
                + this.style.getClickEvent() + ", hoverEvent="
                + this.style.getHoverEvent() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(this.getStyle(), ((PartStyle) o).getStyle());
    }

    @Override
    public int hashCode() {
        return this.style.hashCode();
    }

    public enum StyleType {
        INCLUDE_EVENTS, // Includes click and hover events
        DEFAULT, // The most minimal way to represent a style
        NONE // No styling
    }
}
