CREATE OR REPLACE PACKAGE BODY grepo_demo
IS
  PROCEDURE demo_procedure (
    p_string IN VARCHAR2,
    p_integer IN INTEGER,
    p_result OUT VARCHAR2)
    IS
    BEGIN
        p_result := 'p1:' || p_string || ' p2:' || p_integer;
    END demo_procedure;

  FUNCTION demo_function (
    p_string IN VARCHAR2,
    p_integer IN INTEGER)
    RETURN VARCHAR2
    IS
    BEGIN
    	return 'p1:' || p_string || ' p2:' || p_integer;
  END demo_function;

END grepo_demo;

