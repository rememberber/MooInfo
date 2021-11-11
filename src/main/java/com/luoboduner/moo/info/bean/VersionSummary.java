package com.luoboduner.moo.info.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Version summary
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
@Data
public class VersionSummary implements Serializable {

    private static final long serialVersionUID = 4637273116136790267L;

    /**
     * current version
     */
    private String currentVersion;

    /**
     * version index
     */
    private String versionIndex;

    /**
     * list of history versions
     */
    private List<Version> versionDetailList;

    /**
     * Version Class
     *
     * @author <a href="https://github.com/rememberber">RememBerBer</a>
     * @since 2021/11/08.
     */
    @Data
    public static class Version implements Serializable {

        private static final long serialVersionUID = 4637273116136790268L;

        private String version;

        private String title;

        private String log;

    }

}
