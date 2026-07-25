package com.luoboduner.moo.info.ui;

import java.awt.*;

/**
 * constants about UI
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
public class UiConsts {

    public static final String APP_NAME = "MooInfo";
    public static final String APP_VERSION = "1.1.4";

    public static final int TABLE_ROW_HEIGHT = 36;

    /**
     * Logo-1024*1024
     */
    public static final Image IMAGE_LOGO_1024 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-1024.png"));

    /**
     * Logo-512*512
     */
    public static final Image IMAGE_LOGO_512 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-512.png"));

    /**
     * Logo-256*256
     */
    public static final Image IMAGE_LOGO_256 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-256.png"));

    /**
     * Logo-128*128
     */
    public static final Image IMAGE_LOGO_128 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-128.png"));

    /**
     * Logo-64*64
     */
    public static final Image IMAGE_LOGO_64 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-64.png"));

    /**
     * Logo-48*48
     */
    public static final Image IMAGE_LOGO_48 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-48.png"));

    /**
     * Logo-32*32
     */
    public static final Image IMAGE_LOGO_32 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-32.png"));

    /**
     * Logo-24*24
     */
    public static final Image IMAGE_LOGO_24 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-24.png"));

    /**
     * Logo-16*16
     */
    public static final Image IMAGE_LOGO_16 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icons/logo-16.png"));

    /**
     * GitHub master branch raw content root
     */
    public static final String GITHUB_RAW_MASTER_URL = "https://raw.githubusercontent.com/rememberber/MooInfo/master/";

    /**
     * update checking url
     */
    public static final String CHECK_VERSION_URL = GITHUB_RAW_MASTER_URL + "src/main/resources/version_summary.json";

    /**
     * platform installer download links
     */
    public static final String DOWNLOAD_LINK_INFO_URL = GITHUB_RAW_MASTER_URL + "download_links.json";

    public static final String GITHUB_RELEASES_URL = "https://github.com/rememberber/MooInfo/releases";

    public static final int REFRESH_FAST = 1000;
    public static final int REFRESH_SLOW = 5000;

    public static final int REFRESH_SLOWER = 15_000;


}
