package net.buildtheearth.utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CustomHeads {
    public static final String WHITE_BACKWARD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3M2NmNjZkMzFiODNjZDhiODY0NGMxNTk1OGMxYjczYzhkOTczMjNiODAxMTcwYzFkODg2NGJiNmE4NDZkIn19fQ==";
    public static String EARTH = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkZDRmZTRhNDI5YWJkNjY1ZGZkYjNlMjEzMjFkNmVmYTZhNmI1ZTdiOTU2ZGI5YzVkNTljOWVmYWIyNSJ9fX0=";
    public static String GOLDEN_CUP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAyN2Q0YTg2NDVlYzc4YWZhNjIzZmU0MjkwN2YyZGI2NjQxYmZlZjFiNDk5ZmU3N2IzNjBhMWQwYjlhNjMzYyJ9fX0=";
    public static String WHITE_PLUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBiNTVmNzQ2ODFjNjgyODNhMWMxY2U1MWYxYzgzYjUyZTI5NzFjOTFlZTM0ZWZjYjU5OGRmMzk5MGE3ZTcifX19";
    public static String WHITE_MINUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNlNGI1MzNlNGJhMmRmZjdjMGZhOTBmNjdlOGJlZjM2NDI4YjZjYjA2YzQ1MjYyNjMxYjBiMjVkYjg1YiJ9fX0=";
    public static String WHITE_BLANK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMjE0NGZkY2I1NWMzZmMxYmYxZGU1MWNhYmRmNTJjMzg4M2JjYjU3ODkyMzIyNmJlYjBkODVjYjJkOTgwIn19fQ==";
    public static String WHITE_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQxYTNjOTY1NjIzNDg1MjdkNTc5OGYyOTE2MDkyODFmNzJlMTZkNjExZjFhNzZjMGZhN2FiZTA0MzY2NSJ9fX0=";
    public static String WHITE_0_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2YwOTAxOGY0NmYzNDllNTUzNDQ2OTQ2YTM4NjQ5ZmNmY2Y5ZmRmZDYyOTE2YWVjMzNlYmNhOTZiYjIxYjUifX19";
    public static String WHITE_1_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1MTZmYmFlMTYwNThmMjUxYWVmOWE2OGQzMDc4NTQ5ZjQ4ZjZkNWI2ODNmMTljZjVhMTc0NTIxN2Q3MmNjIn19fQ==";
    public static String WHITE_2_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY5OGFkZDM5Y2Y5ZTRlYTkyZDQyZmFkZWZkZWMzYmU4YTdkYWZhMTFmYjM1OWRlNzUyZTlmNTRhZWNlZGM5YSJ9fX0=";
    public static String WHITE_3_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ5ZTRjZDVlMWI5ZjNjOGQ2Y2E1YTFiZjQ1ZDg2ZWRkMWQ1MWU1MzVkYmY4NTVmZTlkMmY1ZDRjZmZjZDIifX19";
    public static String WHITE_4_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJhM2Q1Mzg5ODE0MWM1OGQ1YWNiY2ZjODc0NjlhODdkNDhjNWMxZmM4MmZiNGU3MmY3MDE1YTM2NDgwNTgifX19";
    public static String WHITE_5_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFmZTM2YzQxMDQyNDdjODdlYmZkMzU4YWU2Y2E3ODA5YjYxYWZmZDYyNDVmYTk4NDA2OTI3NWQxY2JhNzYzIn19fQ==";
    public static String WHITE_6_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FiNGRhMjM1OGI3YjBlODk4MGQwM2JkYjY0Mzk5ZWZiNDQxODc2M2FhZjg5YWZiMDQzNDUzNTYzN2YwYTEifX19";
    public static String WHITE_7_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3NzEyYmEzMjQ5NmM5ZTgyYjIwY2M3ZDE2ZTE2OGIwMzViNmY4OWYzZGYwMTQzMjRlNGQ3YzM2NWRiM2ZiIn19fQ==";
    public static String WHITE_8_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJjMGZkYTlmYTFkOTg0N2EzYjE0NjQ1NGFkNjczN2FkMWJlNDhiZGFhOTQzMjQ0MjZlY2EwOTE4NTEyZCJ9fX0=";
    public static String WHITE_9_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZhYmM2MWRjYWVmYmQ1MmQ5Njg5YzA2OTdjMjRjN2VjNGJjMWFmYjU2YjhiMzc1NWU2MTU0YjI0YTVkOGJhIn19fQ==";
    public static String WHITE_10_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FmM2ZkNDczYTY0OGI4NDdjY2RhMWQyMDc0NDc5YmI3NjcyNzcxZGM0MzUyMjM0NjhlZDlmZjdiNzZjYjMifX19";
    public static String WHITE_11_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhjYWI1M2IwMjA5OGU2ODFhNDZkMWQ3ZjVmZjY5MTc0NmFkZjRlMWZiM2FmZTM1MTZkZDJhZjk0NDU2OSJ9fX0=";
    public static String WHITE_12_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmZkODNiNWJhYWU0Y2I4NTY5NGExNGQ2ZDEzMzQxZWY3MWFhM2Q5MmQzN2RlMDdiZWE3N2IyYzlkYzUzZSJ9fX0=";
    public static String WHITE_13_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFlNTk4NWJlNDg4NmY5ZjE2ZTI0NDdjM2Y0NjEwNTNiNDUxMzQyZDRmYjAxNjZmYjJmODhkZjc0MjIxMzZiNCJ9fX0=";
    public static String WHITE_14_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY4MTQ1NjQzOGFlOWIyZDRkMmJmYWI5Y2YzZmZhOTM1NGVlYmRiM2YwMmNlMjk1NzkyOTM0OGU1Yjg1ZmY5NSJ9fX0=";
    public static String WHITE_15_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE5YzRkYjczNjViMWI4OGIxMjllNzA0MTg0MjEzZmUwNzhkODhiYzNkNGFlM2Q1MjI5MGY2MWQ5NTVkNTEifX19";
    public static String WHITE_16_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY1ZGQwNzliOThmZGFjNDNhMTlhNzk1YmE0NmZkOTdmMjNlYTc3NTdkOTJhZDBhNjlhZGM5NzMyODllNWEifX19";
    public static String WHITE_17_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU1MWJmZThlYmZhYTU4NWE3ODdlMWNiNzcyYzdmZDdkOWE5Mjg2ZDk1ZWZhNTRkNjZmYTgyNzRmMTg4ZiJ9fX0=";
    public static String WHITE_18_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZkZTlhNWEyZDhhMjM3MDcwMTliOWVmNjFkMTY2Mjg2MGUwYjE2NTNkZjZjMjc2MTZiZTJjNzZmY2QxODc1In19fQ==";
    public static String WHITE_19_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJkNDU5MDRkMzRiNjM2YjJmNjQyNjFiM2Q4YmNlZDI1ODI4YzJiOGM0ODIzYjdlMTgzZWU4YTZmMWEyODRkIn19fQ==";
    public static String WHITE_20_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRiOTNjNzIxNTE5ZTE0OTY0OWI3ZTRhZmI2ZDc2Y2ZjODE0NjA4YWU5Yzk1ZTdjM2RiNGJmNGJkYWFjZjMxZSJ9fX0=";
    public static String WHITE_ARROW_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";
    public static String WHITE_ARROW_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";
    public static String LIGHT_GRAY_PLUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19";
    public static String LIGHT_GRAY_MINUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1NmRhYjUzZDRlYTFhNzlhOGU1ZWQ2MzIyYzJkNTZjYjcxNGRkMzVlZGY0Nzg3NjNhZDFhODRhODMxMCJ9fX0=";
    public static String LIGHT_GRAY_BLANK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFmYjhjZTY0MDhhNTg1MTM4NGUxYzJlZjc1Mzg1MWVhYzE4YmE0MDE4MjY2Y2RkNjY5ZGM5NDQ4NzNkNDIifX19";
    public static String LIGHT_GRAY_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmM2VhN2M3YjI2YTA1NGE5ZmJiYjI4Yjk3YTYwODk5OWMyYzczZGY3NWJmNmIyMzQ4ZDdmYjFlNTllODU1In19fQ==";
    public static String LIGHT_GRAY_0_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZhNDU5MTFiMTYyOThjZmNhNGIyMjkxZWVkYTY2NjExM2JjNmYyYTM3ZGNiMmVjZDhjMjc1NGQyNGVmNiJ9fX0=";
    public static String LIGHT_GRAY_1_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FmMWIyODBjYWI1OWY0NDY5ZGFiOWYxYTJhZjc5MjdlZDk2YTgxZGYxZTI0ZDUwYThlMzk4NGFiZmU0MDQ0In19fQ==";
    public static String LIGHT_GRAY_2_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRiMWUxZDQyNjEyM2NlNDBjZDZhNTRiMGY4NzZhZDMwYzA4NTM5Y2Y1YTZlYTYzZTg0N2RjNTA3OTUwZmYifX19";
    public static String LIGHT_GRAY_3_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA0Y2NmOGI1MzMyYzE5NmM5ZWEwMmIyMmIzOWI5OWZhY2QxY2M4MmJmZTNmN2Q3YWVlZGMzYzMzMjkwMzkifX19";
    public static String LIGHT_GRAY_4_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI0ZmMxOGU5NzVmNGYyMjJkODg1MjE2ZTM2M2FkYzllNmQ0NTZhYTI5MDgwZTQ4ZWI0NzE0NGRkYTQzNmY3In19fQ==";
    public static String LIGHT_GRAY_5_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ4YjIyMjM5NzEyZTBhZDU3OWE2MmFlNGMxMTUxMDNlNzcyODgyNWUxNzUwOGFjZDZjYzg5MTc0ZWU4MzgifX19";
    public static String LIGHT_GRAY_6_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVlZmJhZDE2NzEyYTA1Zjk4ZTRmMGRlNWI0NDg2YWYzOTg3YjQ2ZWE2YWI0ZTNiZTkzZDE0YTgzMmM1NmUifX19";
    public static String LIGHT_GRAY_7_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNlNjlmYTk0MmRmM2Q1ZWE1M2EzYTk3NDkxNjE3NTEwOTI0YzZiOGQ3YzQzNzExOTczNzhhMWNmMmRlZjI3In19fQ==";
    public static String LIGHT_GRAY_8_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2QxODRmZDRhYjUxZDQ2MjJmNDliNTRjZTdhMTM5NWMyOWYwMmFkMzVjZTVhYmQ1ZDNjMjU2MzhmM2E4MiJ9fX0=";
    public static String LIGHT_GRAY_9_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIyNDU0YTVmYWEyNWY3YzRmNTc3MWQ1MmJiNGY1NWRlYjE5MzlmNzVlZmQ4ZTBhYzQyMTgxMmJhM2RjNyJ9fX0=";
    public static String LIGHT_GRAY_10_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZiYWY3ODZjYmI2NmZkOTQzY2I0NWIxZmE1MmYzNjI4OWEzOWYyZDk4NThkOWJlYmUxZTFhMzcwZDdkZmNjIn19fQ==";
    public static String LIGHT_GRAY_11_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmRlNDI5YmM3MmIyY2M3ZmY3MGMxZDZjOWYxMTE2ZWMwNzExMmYxZjY0YzU4YmQ4YjljNDgwODNmZTIwMSJ9fX0=";
    public static String LIGHT_GRAY_12_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWNlMDg0MWYwNjExZDg1NWQ1MGUxNmRkYzNkODM0MDZhN2MwNjRhZDYyNzFkNWM2MzE3M2MwNWQzZDNjYjAifX19";
    public static String LIGHT_GRAY_13_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViYTZlOTBkZDg4MTYyNjE2MWRlYjllMWE4NzY1YTFhMzRiZjc3MTE3OTMxYWJlYjU3NzhiNTQ5ZmQyYiJ9fX0=";
    public static String LIGHT_GRAY_14_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdjYjU4MjAxNGM2YTFhOTYwYjE3MDQ5NDliMTYyYTk4ZDdmNjU1ZmI2NmM2ZjE4MTU0ZWNjZGE2YTVmYTY1In19fQ==";
    public static String LIGHT_GRAY_15_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGM0OTYwMWVmNjlkNjNkY2YxZDlmOGVkMThhMzhiNGI3ZjM0NGY3Njc4YjM2NTU1OWE0NzM5ZmNmYmZkOTYyIn19fQ==";
    public static String LIGHT_GRAY_16_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjkwZmVmY2YyZGNhMTlhNjcyYzYxNTllMjg3YzRlZGVmNGU5MWY0NTllODNmZmRhZjYxNmI4ZTg4Y2QyYTgifX19";
    public static String LIGHT_GRAY_17_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRlZTU4MTg5ODMyOGMzMTZkMjY2ZWQ2NjRiY2M0ZDI5MzNkOTNiZTc5Mjk4Yzc5OGI3ODlkODNkMzRiM2FlIn19fQ==";
    public static String LIGHT_GRAY_18_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhkOTc3ODhiOTRjMzU2YzJmZjQ0NWY0NGY5NTM0ZmU0OGNiNWQyMTU0N2Q2NGZkYmI5OGFjYzFjNWRlZmUifX19";
    public static String LIGHT_GRAY_19_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM1YjU5ZDk4YzkyNzRmOWI4NDMzNmNmMWFjYWUxNWIxYmU2OWY0MzQ4OGQ2NDI2YzhlMzQzZWMzN2FiMTI2In19fQ==";
    public static String LIGHT_GRAY_20_B64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRhOTJlZGVkY2FmMTlmYmRjYjUwNWIyMGQ2NzQ3MmJkYTc4MWYyZGQzY2Y3MzcyZmFmOTcyOWQ5NzMxYiJ9fX0=";
    public static String WOODEN_LETTER_A = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=";
    public static String WOODEN_LETTER_B = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBjMWI1ODRmMTM5ODdiNDY2MTM5Mjg1YjJmM2YyOGRmNjc4NzEyM2QwYjMyMjgzZDg3OTRlMzM3NGUyMyJ9fX0=";
    public static String WOODEN_LETTER_C = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJlOTgzZWM0NzgwMjRlYzZmZDA0NmZjZGZhNDg0MjY3NjkzOTU1MWI0NzM1MDQ0N2M3N2MxM2FmMThlNmYifX19";
    public static String WOODEN_LETTER_D = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5M2RjMGQ0YzVlODBmZjlhOGEwNWQyZmNmZTI2OTUzOWNiMzkyNzE5MGJhYzE5ZGEyZmNlNjFkNzEifX19";
    public static String WOODEN_LETTER_E = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0=";
    public static String WOODEN_LETTER_F = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE4M2JhYjUwYTMyMjQwMjQ4ODZmMjUyNTFkMjRiNmRiOTNkNzNjMjQzMjU1OWZmNDllNDU5YjRjZDZhIn19fQ==";
    public static String WOODEN_LETTER_G = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNhM2YzMjRiZWVlZmI2YTBlMmM1YjNjNDZhYmM5MWNhOTFjMTRlYmE0MTlmYTQ3NjhhYzMwMjNkYmI0YjIifX19";
    public static String WOODEN_LETTER_H = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmMzQ2MmE0NzM1NDlmMTQ2OWY4OTdmODRhOGQ0MTE5YmM3MWQ0YTVkODUyZTg1YzI2YjU4OGE1YzBjNzJmIn19fQ==";
    public static String WOODEN_LETTER_I = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19";
    public static String WOODEN_LETTER_J = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E3OWRiOTkyMzg2N2U2OWMxZGJmMTcxNTFlNmY0YWQ5MmNlNjgxYmNlZGQzOTc3ZWViYmM0NGMyMDZmNDkifX19";
    public static String WOODEN_LETTER_K = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ2MWIzOGM4ZTQ1NzgyYWRhNTlkMTYxMzJhNDIyMmMxOTM3NzhlN2Q3MGM0NTQyYzk1MzYzNzZmMzdiZTQyIn19fQ==";
    public static String WOODEN_LETTER_L = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5ZjUwYjQzMmQ4NjhhZTM1OGUxNmY2MmVjMjZmMzU0MzdhZWI5NDkyYmNlMTM1NmM5YWE2YmIxOWEzODYifX19";
    public static String WOODEN_LETTER_M = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljNDVhMjRhYWFiZjQ5ZTIxN2MxNTQ4MzIwNDg0OGE3MzU4MmFiYTdmYWUxMGVlMmM1N2JkYjc2NDgyZiJ9fX0=";
    public static String WOODEN_LETTER_N = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViOGIzZDhjNzdkZmI4ZmJkMjQ5NWM4NDJlYWM5NGZmZmE2ZjU5M2JmMTVhMjU3NGQ4NTRkZmYzOTI4In19fQ==";
    public static String WOODEN_LETTER_O = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDExZGUxY2FkYjJhZGU2MTE0OWU1ZGVkMWJkODg1ZWRmMGRmNjI1OTI1NWIzM2I1ODdhOTZmOTgzYjJhMSJ9fX0=";
    public static String WOODEN_LETTER_P = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhNzk4OWI1ZDZlNjIxYTEyMWVlZGFlNmY0NzZkMzUxOTNjOTdjMWE3Y2I4ZWNkNDM2MjJhNDg1ZGMyZTkxMiJ9fX0=";
    public static String WOODEN_LETTER_Q = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM2MDlmMWZhZjgxZWQ0OWM1ODk0YWMxNGM5NGJhNTI5ODlmZGE0ZTFkMmE1MmZkOTQ1YTU1ZWQ3MTllZDQifX19";
    public static String WOODEN_LETTER_R = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVjZWQ5OTMxYWNlMjNhZmMzNTEzNzEzNzliZjA1YzYzNWFkMTg2OTQzYmMxMzY0NzRlNGU1MTU2YzRjMzcifX19";
    public static String WOODEN_LETTER_S = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0MWM2MDU3MmM1MzNlOTNjYTQyMTIyODkyOWU1NGQ2Yzg1NjUyOTQ1OTI0OWMyNWMzMmJhMzNhMWIxNTE3In19fQ==";
    public static String WOODEN_LETTER_T = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU2MmU4YzFkNjZiMjFlNDU5YmU5YTI0ZTVjMDI3YTM0ZDI2OWJkY2U0ZmJlZTJmNzY3OGQyZDNlZTQ3MTgifX19";
    public static String WOODEN_LETTER_U = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA3ZmJjMzM5ZmYyNDFhYzNkNjYxOWJjYjY4MjUzZGZjM2M5ODc4MmJhZjNmMWY0ZWZkYjk1NGY5YzI2In19fQ==";
    public static String WOODEN_LETTER_V = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTEzODYzOGZlZGI1MzRkNzk5Mjg4NzZiYWJhMjYxYzdhNjRiYTc5YzQyNGRjYmFmY2M5YmFjNzAxMGI4In19fQ==";
    public static String WOODEN_LETTER_W = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY5YWQxYTg4ZWQyYjA3NGUxMzAzYTEyOWY5NGU0YjcxMGNmM2U1YjRkOTk1MTYzNTY3ZjY4NzE5YzNkOTc5MiJ9fX0=";
    public static String WOODEN_LETTER_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2Nzg3YmEzMjU2NGU3YzJmM2EwY2U2NDQ5OGVjYmIyM2I4OTg0NWU1YTY2YjVjZWM3NzM2ZjcyOWVkMzcifX19";
    public static String WOODEN_LETTER_Y = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyZmIzODhlMzMyMTJhMjQ3OGI1ZTE1YTk2ZjI3YWNhNmM2MmFjNzE5ZTFlNWY4N2ExY2YwZGU3YjE1ZTkxOCJ9fX0=";
    public static String WOODEN_LETTER_Z = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA1ODJiOWI1ZDk3OTc0YjExNDYxZDYzZWNlZDg1ZjQzOGEzZWVmNWRjMzI3OWY5YzQ3ZTFlMzhlYTU0YWU4ZCJ9fX0=";
    public static String WOODEN_LETTER_QUESTION_MARK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";
    public static String STONE_LETTER_A = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFjNThiMWEzYjUzYjk0ODFlMzE3YTFlYTRmYzVlZWQ2YmFmY2E3YTI1ZTc0MWEzMmU0ZTNjMjg0MTI3OGMifX19";
    public static String STONE_LETTER_B = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDRjNzExNTcxZTdlMjE0ZWU3OGRmZTRlZTBlMTI2M2I5MjUxNmU0MThkZThmYzhmMzI1N2FlMDkwMTQzMSJ9fX0=";
    public static String STONE_LETTER_C = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZmNWFhYmVhZDZmZWFmYWFlY2Y0NDIyY2RkNzgzN2NiYjM2YjAzYzk4NDFkZDFiMWQyZDNlZGI3ODI1ZTg1MSJ9fX0=";
    public static String STONE_LETTER_D = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkzZTYyMmI1ODE5NzU3OTJmN2MxMTllYzZmNDBhNGYxNmU1NTJiYjk4Nzc2YjBjN2FlMmJkZmQ0MTU0ZmU3In19fQ==";
    public static String STONE_LETTER_E = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE1N2Q2NWIxOTkyMWM3NjBmZjQ5MTBiMzQwNDQ1NWI5YzJlZTM2YWZjMjAyZDg1MzhiYWVmZWM2NzY5NTMifX19";
    public static String STONE_LETTER_F = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU0Y2YyNjFiMmNkNmFiNTRiMGM2MjRmOGY2ZmY1NjVhN2I2M2UyOGUzYjUwYzZkYmZiNTJiNWYwZDdjZjlmIn19fQ==";
    public static String STONE_LETTER_G = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNjOWY4YTc0Y2EwMWJhOGM1NGRlMWVkYzgyZTFmYzA3YTgzOTIzZTY2NTc0YjZmZmU2MDY5MTkyNDBjNiJ9fX0=";
    public static String STONE_LETTER_H = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhjNThjNTA5MDM0NjE3YmY4MWVlMGRiOWJlMGJhM2U4NWNhMTU1NjgxNjM5MTRjODc2NjllZGIyZmQ3In19fQ==";
    public static String STONE_LETTER_I = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI0NjMyM2M5ZmIzMTkzMjZlZTJiZjNmNWI2M2VjM2Q5OWRmNzZhMTI0MzliZjBiNGMzYWIzMmQxM2ZkOSJ9fX0=";
    public static String STONE_LETTER_J = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU4NDU2Y2Q5YmI4YTdlOTc4NTkxYWUwY2IyNmFmMWFhZGFkNGZhN2ExNjcyNWIyOTUxNDVlMDliZWQ4MDY0In19fQ==";
    public static String STONE_LETTER_K = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY0OWZiNzA4MzY5ZTdiYzI5NDRhZDcwNjk2M2ZiNmFjNmNlNmQ0YzY3MDgxZGRhZGVjZmU1ZGE1MSJ9fX0=";
    public static String STONE_LETTER_L = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4NGY3NTQxNmU4NTNhNzRmNmM3MGZjN2UxMDkzZDUzOTYxODc5OTU1YjQzM2JkOGM3YzZkNWE2ZGYifX19";
    public static String STONE_LETTER_M = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmZGU5MWIxOWI5MzA5OTEzNzI0ZmVhOWU4NTMxMTI3MWM2N2JjYjc4NTc4ZDQ2MWJmNjVkOTYxMzA3NCJ9fX0=";
    public static String STONE_LETTER_N = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM3Yzk3MmU2Nzg1ZDZiMGFjZWI3NzlhYmRkNzcwMmQ5ODM0MWMyNGMyYTcxZTcwMjkzMGVjYTU4MDU1In19fQ==";
    public static String STONE_LETTER_O = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODA3M2JiNDRmOTM0NWY5YmIzMWE2NzkwMjdlNzkzOWU0NjE4NDJhOGMyNzQ4NmQ3YTZiODQyYzM5ZWIzOGM0ZSJ9fX0=";
    public static String STONE_LETTER_P = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRiMjMxYThkNTU4NzBjZmI1YTlmNGU2NWRiMDZkZDdmOGUzNDI4MmYxNDE2Zjk1ODc4YjE5YWNjMzRhYzk1In19fQ==";
    public static String STONE_LETTER_Q = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZlZGQ2ZjllZmRiMTU2Yjg2OTM1Njk5YjJiNDgzNGRmMGY1ZDIxNDUxM2MwMWQzOGFmM2JkMDMxY2JjYzkyIn19fQ==";
    public static String STONE_LETTER_R = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzAzYTFjZDU4M2NiYmZmZGUwOGY5NDNlNTZhYzNlM2FmYWZlY2FlZGU4MzQyMjFhODFlNmRiNmM2NDY2N2Y3ZCJ9fX0=";
    public static String STONE_LETTER_S = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY1NzJlNjU1NzI1ZDc4Mzc1YTk4MTdlYjllZThiMzc4MjljYTFmZWE5M2I2MDk1Y2M3YWExOWU1ZWFjIn19fQ==";
    public static String STONE_LETTER_T = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA4YzllZjNhMzc1MWUyNTRlMmFmMWFkOGI1ZDY2OGNjZjVjNmVjM2VhMjY0MTg3N2NiYTU3NTgwN2QzOSJ9fX0=";
    public static String STONE_LETTER_U = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVhNmUzYWU1YWU2MjU5MjM1MjQ4MzhmYWM5ZmVmNWI0MjUyN2Y1MDI3YzljYTE0OWU2YzIwNzc5MmViIn19fQ==";
    public static String STONE_LETTER_V = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc1MTIxZjdkOWM2OGRhMGU1YjZhOTZhYzYxNTI5OGIxMmIyZWU1YmQxOTk4OTQzNmVlNjQ3ODc5ZGE1YiJ9fX0=";
    public static String STONE_LETTER_W = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjdlMTY1YzNlZGM1NTQxZDQ2NTRjNDcyODg3MWU2OTA4ZjYxM2ZjMGVjNDZlODIzYzk2ZWFjODJhYzYyZTYyIn19fQ==";
    public static String STONE_LETTER_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkxOWQxNTk0YmY4MDlkYjdiNDRiMzc4MmJmOTBhNjlmNDQ5YTg3Y2U1ZDE4Y2I0MGViNjUzZmRlYzI3MjIifX19";
    public static String STONE_LETTER_Y = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM1NDI0YmI4NjMwNWQ3NzQ3NjA0YjEzZTkyNGQ3NGYxZWZlMzg5MDZlNGU0NThkZDE4ZGNjNjdiNmNhNDgifX19";
    public static String STONE_LETTER_Z = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU5MTIwMGRmMWNhZTUxYWNjMDcxZjg1YzdmN2Y1Yjg0NDlkMzliYjMyZjM2M2IwYWE1MWRiYzg1ZDEzM2UifX19";
    public static String STONE_QUESTION_MARK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==";
    public static String CHECKMARK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
    public static String GREEN_PLUS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19";

    public static ItemStack getCurrentPageItem(int currentPage) {
        return getWhiteNumberHead(currentPage, "§eCurrent Page §7- §f" + (currentPage), null);
    }

    public static ItemStack getPreviousPageItem(int currentPage) {
        if (currentPage <= 1)
            return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);

        return Item.createCustomHeadBase64(WHITE_ARROW_LEFT, "§ePrevious Page §7- §f" + (currentPage - 1), null);
    }

    public static ItemStack getNextPageItem(int currentPage, boolean hasNextPage) {
        if (!hasNextPage)
            return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);

        return Item.createCustomHeadBase64(WHITE_ARROW_RIGHT, "§eNext Page §7- §f" + (currentPage + 1), null);
    }

    public static ItemStack getBackItem(){
        return Item.createCustomHeadBase64(WHITE_BACKWARD, "§eBack", null);
    }

    public static ItemStack getCounterCurrentValueItem(SliderColor sliderColor, String name, int value, String valueType) {
        String sliderName = "§e" + name + ": §7§l" + value;
        if (valueType != null)
            sliderName += " " + valueType;

        switch (sliderColor) {
            default:
            case WHITE:
                return getWhiteNumberHead(value, sliderName, null);
            case LIGHT_GRAY:
                return getLightGrayNumberHead(value, sliderName, null);
        }
    }

    public static ItemStack getCounterPlusItem(SliderColor sliderColor, String name, int value, int maxValue) {
        if (value >= maxValue)
            switch (sliderColor) {
                case WHITE:
                    return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);
                case LIGHT_GRAY:
                    return Item.createCustomHeadBase64(LIGHT_GRAY_BLANK, " ", null);
            }

        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(WHITE_PLUS, "§a§l+ §e" + name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(LIGHT_GRAY_PLUS, "§a§l+ §e" + name, null);
        }
    }

    public static ItemStack getCounterMinusItem(SliderColor sliderColor, String name, int value, int minValue) {
        if (value <= minValue)
            switch (sliderColor) {
                case WHITE:
                    return Item.createCustomHeadBase64(WHITE_BLANK, " ", null);
                case LIGHT_GRAY:
                    return Item.createCustomHeadBase64(LIGHT_GRAY_BLANK, " ", null);
            }

        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(WHITE_MINUS, "§c§l- §e" + name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(LIGHT_GRAY_MINUS, "§c§l- §e" + name, null);
        }
    }

    public static ItemStack getXItem(SliderColor sliderColor, String name) {
        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(WHITE_X, name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(LIGHT_GRAY_X, name, null);
        }
    }

    public static ItemStack getBlankItem(SliderColor sliderColor, String name) {
        switch (sliderColor) {
            default:
            case WHITE:
                return Item.createCustomHeadBase64(WHITE_BLANK, name, null);
            case LIGHT_GRAY:
                return Item.createCustomHeadBase64(LIGHT_GRAY_BLANK, name, null);
        }
    }

    public static ItemStack getCheckmarkItem(String name) {
        return Item.createCustomHeadBase64(CHECKMARK, name, null);
    }

    public static ItemStack getWhiteNumberHead(int number, String headName, ArrayList<String> lore) {
        String b64;
        switch (number) {
            case 0:
                b64 = WHITE_0_B64;
                break;
            case 1:
                b64 = WHITE_1_B64;
                break;
            case 2:
                b64 = WHITE_2_B64;
                break;
            case 3:
                b64 = WHITE_3_B64;
                break;
            case 4:
                b64 = WHITE_4_B64;
                break;
            case 5:
                b64 = WHITE_5_B64;
                break;
            case 6:
                b64 = WHITE_6_B64;
                break;
            case 7:
                b64 = WHITE_7_B64;
                break;
            case 8:
                b64 = WHITE_8_B64;
                break;
            case 9:
                b64 = WHITE_9_B64;
                break;
            case 10:
                b64 = WHITE_10_B64;
                break;
            case 11:
                b64 = WHITE_11_B64;
                break;
            case 12:
                b64 = WHITE_12_B64;
                break;
            case 13:
                b64 = WHITE_13_B64;
                break;
            case 14:
                b64 = WHITE_14_B64;
                break;
            case 15:
                b64 = WHITE_15_B64;
                break;
            case 16:
                b64 = WHITE_16_B64;
                break;
            case 17:
                b64 = WHITE_17_B64;
                break;
            case 18:
                b64 = WHITE_18_B64;
                break;
            case 19:
                b64 = WHITE_19_B64;
                break;
            case 20:
                b64 = WHITE_20_B64;
                break;

            default:
                b64 = WHITE_BLANK;
                break;
        }
        return Item.createCustomHeadBase64(b64, headName, lore);
    }

    public static ItemStack getLightGrayNumberHead(int number, String headName, ArrayList<String> lore) {
        String b64;
        switch (number) {
            case 0:
                b64 = LIGHT_GRAY_0_B64;
                break;
            case 1:
                b64 = LIGHT_GRAY_1_B64;
                break;
            case 2:
                b64 = LIGHT_GRAY_2_B64;
                break;
            case 3:
                b64 = LIGHT_GRAY_3_B64;
                break;
            case 4:
                b64 = LIGHT_GRAY_4_B64;
                break;
            case 5:
                b64 = LIGHT_GRAY_5_B64;
                break;
            case 6:
                b64 = LIGHT_GRAY_6_B64;
                break;
            case 7:
                b64 = LIGHT_GRAY_7_B64;
                break;
            case 8:
                b64 = LIGHT_GRAY_8_B64;
                break;
            case 9:
                b64 = LIGHT_GRAY_9_B64;
                break;
            case 10:
                b64 = LIGHT_GRAY_10_B64;
                break;
            case 11:
                b64 = LIGHT_GRAY_11_B64;
                break;
            case 12:
                b64 = LIGHT_GRAY_12_B64;
                break;
            case 13:
                b64 = LIGHT_GRAY_13_B64;
                break;
            case 14:
                b64 = LIGHT_GRAY_14_B64;
                break;
            case 15:
                b64 = LIGHT_GRAY_15_B64;
                break;
            case 16:
                b64 = LIGHT_GRAY_16_B64;
                break;
            case 17:
                b64 = LIGHT_GRAY_17_B64;
                break;
            case 18:
                b64 = LIGHT_GRAY_18_B64;
                break;
            case 19:
                b64 = LIGHT_GRAY_19_B64;
                break;
            case 20:
                b64 = LIGHT_GRAY_20_B64;
                break;

            default:
                b64 = LIGHT_GRAY_BLANK;
                break;
        }
        return Item.createCustomHeadBase64(b64, headName, lore);
    }

    public static ItemStack getLetterHead(String letter, LetterType letterType, String headName, ArrayList<String> lore) {
        letter = letter.toUpperCase();

        String b64;

        if(letterType == LetterType.WOODEN) {
            switch (letter) {
                case "A":
                    b64 = WOODEN_LETTER_A;
                    break;
                case "B":
                    b64 = WOODEN_LETTER_B;
                    break;
                case "C":
                    b64 = WOODEN_LETTER_C;
                    break;
                case "D":
                    b64 = WOODEN_LETTER_D;
                    break;
                case "E":
                    b64 = WOODEN_LETTER_E;
                    break;
                case "F":
                    b64 = WOODEN_LETTER_F;
                    break;
                case "G":
                    b64 = WOODEN_LETTER_G;
                    break;
                case "H":
                    b64 = WOODEN_LETTER_H;
                    break;
                case "I":
                    b64 = WOODEN_LETTER_I;
                    break;
                case "J":
                    b64 = WOODEN_LETTER_J;
                    break;
                case "K":
                    b64 = WOODEN_LETTER_K;
                    break;
                case "L":
                    b64 = WOODEN_LETTER_L;
                    break;
                case "M":
                    b64 = WOODEN_LETTER_M;
                    break;
                case "N":
                    b64 = WOODEN_LETTER_N;
                    break;
                case "O":
                    b64 = WOODEN_LETTER_O;
                    break;
                case "P":
                    b64 = WOODEN_LETTER_P;
                    break;
                case "Q":
                    b64 = WOODEN_LETTER_Q;
                    break;
                case "R":
                    b64 = WOODEN_LETTER_R;
                    break;
                case "S":
                    b64 = WOODEN_LETTER_S;
                    break;
                case "T":
                    b64 = WOODEN_LETTER_T;
                    break;
                case "U":
                    b64 = WOODEN_LETTER_U;
                    break;
                case "V":
                    b64 = WOODEN_LETTER_V;
                    break;
                case "W":
                    b64 = WOODEN_LETTER_W;
                    break;
                case "X":
                    b64 = WOODEN_LETTER_X;
                    break;
                case "Y":
                    b64 = WOODEN_LETTER_Y;
                    break;
                case "Z":
                    b64 = WOODEN_LETTER_Z;
                    break;
                default:
                    b64 = WOODEN_LETTER_QUESTION_MARK;
                    break;
            }
        } else {
            switch (letter) {
                case "A":
                    b64 = STONE_LETTER_A;
                    break;
                case "B":
                    b64 = STONE_LETTER_B;
                    break;
                case "C":
                    b64 = STONE_LETTER_C;
                    break;
                case "D":
                    b64 = STONE_LETTER_D;
                    break;
                case "E":
                    b64 = STONE_LETTER_E;
                    break;
                case "F":
                    b64 = STONE_LETTER_F;
                    break;
                case "G":
                    b64 = STONE_LETTER_G;
                    break;
                case "H":
                    b64 = STONE_LETTER_H;
                    break;
                case "I":
                    b64 = STONE_LETTER_I;
                    break;
                case "J":
                    b64 = STONE_LETTER_J;
                    break;
                case "K":
                    b64 = STONE_LETTER_K;
                    break;
                case "L":
                    b64 = STONE_LETTER_L;
                    break;
                case "M":
                    b64 = STONE_LETTER_M;
                    break;
                case "N":
                    b64 = STONE_LETTER_N;
                    break;
                case "O":
                    b64 = STONE_LETTER_O;
                    break;
                case "P":
                    b64 = STONE_LETTER_P;
                    break;
                case "Q":
                    b64 = STONE_LETTER_Q;
                    break;
                case "R":
                    b64 = STONE_LETTER_R;
                    break;
                case "S":
                    b64 = STONE_LETTER_S;
                    break;
                case "T":
                    b64 = STONE_LETTER_T;
                    break;
                case "U":
                    b64 = STONE_LETTER_U;
                    break;
                case "V":
                    b64 = STONE_LETTER_V;
                    break;
                case "W":
                    b64 = STONE_LETTER_W;
                    break;
                case "X":
                    b64 = STONE_LETTER_X;
                    break;
                case "Y":
                    b64 = STONE_LETTER_Y;
                    break;
                case "Z":
                    b64 = STONE_LETTER_Z;
                    break;
                default:
                    b64 = STONE_QUESTION_MARK;
                    break;
            }
        }

        return Item.createCustomHeadBase64(b64, headName, lore);
    }

    public enum SliderColor {
        WHITE, LIGHT_GRAY
    }

    public enum LetterType {
        WOODEN, STONE;
    }
}
