CREATE OR REPLACE PACKAGE grepo_demo
IS
  PROCEDURE demo_procedure (
     p_string IN VARCHAR2,
     p_integer IN INTEGER,
     p_result OUT VARCHAR2);

  FUNCTION demo_function (
    p_string IN VARCHAR2,
    p_integer IN INTEGER)
    RETURN VARCHAR2;

END grepo_demo;
