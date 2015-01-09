package br.com.uwant.utils;

import android.util.Log;

/**
 * Classe utilitária para ações relacionadas a entidades de debug do sistema.
 */
public abstract class DebugUtil {

    /**
     * Variável responsável por ativar o monitoramento através de log's no LogCat.
     */
    public static final boolean DEBUG_LOG = true;

    /**
     * Variável responsável por ativar o modo debug para não envio de requisições
     * realizando o processo do retorno do WS localmente.
     */
    public static final boolean DEBUG_WITHOUT_REQUEST = false;

    /**
     * Tag padrão para exibição do log no LogCat.
     */
    private static final String DEFAULT_TAG = "uWant-Debug";

    /**
     * Método auxiliar para registro de log com tag de DEBUG.
     * @param tag
     * @param message
     */
    public static void debug(String tag, String message) {
        if (DEBUG_LOG) {
            Log.d(tag, message);
        }
    }

    /**
     * Método auxiliar para registro de log com tag de DEBUG.
     * @param message
     */
    public static void debug(String message) {
        debug(DEFAULT_TAG, message);
    }

    /**
     * Método auxiliar para registro de log com tag de INFO.
     * @param tag
     * @param message
     */
    public static void info(String tag, String message) {
        if (DEBUG_LOG) {
            Log.i(tag, message);
        }
    }

    /**
     * Método auxiliar para registro de log com tag de INFO.
     * @param message
     */
    public static void info(String message) {
        info(DEFAULT_TAG, message);
    }

    /**
     * Método auxiliar para registro de log com tag de ERROR.
     * @param tag
     * @param message
     */
    public static void error(String tag, String message) {
        if (DEBUG_LOG) {
            Log.w(tag, message);
        }
    }

    /**
     * Método auxiliar para registro de log com tag de ERROR.
     * @param message
     */
    public static void error(String message) {
        error(DEFAULT_TAG, message);
    }

}
