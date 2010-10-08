CREATE OR REPLACE PACKAGE BODY grepo_test
IS
  PROCEDURE simple_proc1 (
    p_string IN VARCHAR2,
    p_integer IN INTEGER,
    p_result OUT VARCHAR2)
    IS
    BEGIN
        p_result := 'p1=' || p_string || ' p2=' || p_integer;
    END simple_proc1;

  PROCEDURE simple_proc2 (
	p_string IN VARCHAR2,
	p_integer IN OUT INTEGER)
	IS
	BEGIN
	   p_integer := 99;
  END simple_proc2;

  FUNCTION simple_function (
    p_string IN VARCHAR2,
    p_integer IN INTEGER)
    RETURN VARCHAR2
    IS
    BEGIN
       return 'p1=' || p_string || ' p2=' || p_integer;
  END simple_function;

  PROCEDURE cursor_proc (
    p_string IN VARCHAR2,
    p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
       open p_result FOR
       select p_string as value from dual;
  END cursor_proc;
  

END grepo_test;

