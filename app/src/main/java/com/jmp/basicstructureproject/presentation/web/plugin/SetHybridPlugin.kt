package com.jmp.basicstructureproject.presentation.web

/**
 * 하이브리드 플러그인 어노테이션
 *
 * @date 2020.01.23
 * @author HNP
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class SetHybridPlugIn(
    /**
     * Hybrid 플러그인 커멘드 키
     * @return
     */
    val pluginId: String
)
