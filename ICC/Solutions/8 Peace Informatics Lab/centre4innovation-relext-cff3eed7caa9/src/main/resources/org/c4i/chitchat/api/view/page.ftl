<#-- @ftlvariable name="" type="org.c4i.chitchat.api.view.PageView" -->
<#import "${contentFtl}" as content>

<!DOCTYPE html>
<!--

  ____      _           _
 |  _ \ ___| | _____  _| |_
 | |_) / _ \ |/ _ \ \/ / __|
 |  _ <  __/ |  __/>  <| |_
 |_| \_\___|_|\___/_/\_\\__|


-->
<html lang="en">
<!-- begin::Head -->
<head>
    <meta charset="utf-8"/>
    <title>
        <@content.pageTitle />
    </title>
    <meta name="description" content="Script information retrieval and conversations">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link href="/asset5/vendors/base/fonts/poppins.css" rel="stylesheet" type="text/css"/>
    <link href="/asset5/vendors/base/fonts/roboto.css" rel="stylesheet" type="text/css"/>
    <!--begin::Base Styles -->
    <link href="/asset5/vendors/base/vendors.bundle.css" rel="stylesheet" type="text/css"/>
    <link href="/asset5/demo/default/base/style.bundle.css" rel="stylesheet" type="text/css"/>
    <!--end::Base Styles -->

    <link rel="stylesheet" href="/asset5/vendors/custom/codemirror/lib/codemirror.css">
    <link rel="stylesheet" href="/asset5/vendors/custom/codemirror/addon/dialog/dialog.css">
    <link rel="stylesheet" href="/asset5/vendors/custom/codemirror/addon/fold/foldgutter.css">
    <link rel="stylesheet" href="/asset5/vendors/custom/codemirror/addon/scroll/simplescrollbars.css">
    <link rel="stylesheet" href="/asset5/vendors/custom/codemirror/addon/hint/show-hint.css">
    <link rel="stylesheet" href="/asset5/app/js/chitchatscript.css">
    <link rel="stylesheet" href="/asset5/app/js/mddoc.css">
    <link rel="stylesheet" href="/asset5/vendors/custom/leaflet/leaflet.css" />
    <link rel="stylesheet" href="/asset5/vendors/custom/leaflet/markercluster/MarkerCluster.css" />
    <link rel="stylesheet" href="/asset5/vendors/custom/leaflet/markercluster/MarkerCluster.Default.css" />
    <link rel="stylesheet" href="/asset5/vendors/custom/cytoscape/cytoscape-panzoom.css" />


    <style>
        .hxlogo {
            width: 150px;
            font-size: 20px;
            display: inline-block;
        }

        .hxlogo a {
            text-decoration: none;
        }

        .hxlogo a:hover {
            text-decoration: none;
            color: #bea8f2;
        }

        .truncate {
            /*width: 250px;*/
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .m-nav__link, dropdown-item {
            cursor: pointer;
        }

        .leaflet-map-pane canvas {
            z-index: 4;
        }

        .m-portlet.m-portlet--fullscreen {
            z-index: 6;
        }
    </style>

    <link rel="shortcut icon" href="/asset5/app/media/img/favicon.ico"/>
</head>
<!-- end::Head -->
<!-- end::Body -->
<body class="m-page--fluid m--skin- m-content--skin-light2 m-header--fixed m-header--fixed-mobile m-aside-left--enabled m-aside-left--skin-dark m-aside-left--offcanvas m-footer--push m-aside--offcanvas-default m-aside-left--fixed">
<!-- begin:: Page -->
<div class="m-grid m-grid--hor m-grid--root m-page">
    <!-- BEGIN: Header -->
    <header id="m_header" class="m-grid__item    m-header " m-minimize-offset="200" m-minimize-mobile-offset="200">
        <div class="m-container m-container--fluid m-container--full-height">
            <div class="m-stack m-stack--ver m-stack--desktop">
                <!-- BEGIN: Brand -->
                <div class="m-stack__item m-brand  m-brand--skin-dark ">
                    <div class="m-stack m-stack--ver m-stack--general">
                        <div class="m-stack__item m-stack__item--middle m-brand__logo hxlogo">
                            <a href="/api/v1/ui/dashboard">
                                <i class="la la-share-alt la-3x text-brand" style="margin-right: 4px; font-size: xx-large; vertical-align: text-bottom;"></i> Relext
                            </a>
                        </div>
                        <div class="m-stack__item m-stack__item--middle m-brand__tools">
                            <!-- BEGIN: Left Aside Minimize Toggle -->
                            <a href="javascript:;" id="m_aside_left_minimize_toggle" class="m-brand__icon m-brand__toggler m-brand__toggler--left m--visible-desktop-inline-block
					 ">
                                <span></span>
                            </a>
                            <!-- END -->
                            <!-- BEGIN: Responsive Aside Left Menu Toggler -->
                            <a href="javascript:;" id="m_aside_left_offcanvas_toggle"
                               class="m-brand__icon m-brand__toggler m-brand__toggler--left m--visible-tablet-and-mobile-inline-block">
                                <span></span>
                            </a>
                            <!-- END -->
                            <!-- BEGIN: Topbar Toggler -->
                            <a id="m_aside_header_topbar_mobile_toggle" href="javascript:;"
                               class="m-brand__icon m--visible-tablet-and-mobile-inline-block">
                                <i class="flaticon-more"></i>
                            </a>
                            <!-- BEGIN: Topbar Toggler -->
                        </div>
                    </div>
                </div>
                <!-- END: Brand -->
                <div class="m-stack__item m-stack__item--fluid m-header-head" id="m_header_nav">
                    <!-- BEGIN: Horizontal Menu -->
                    <button class="m-aside-header-menu-mobile-close  m-aside-header-menu-mobile-close--skin-dark "
                            id="m_aside_header_menu_mobile_close_btn">
                        <i class="la la-close"></i>
                    </button>
                    <!-- END: Horizontal Menu -->                                <!-- BEGIN: Topbar -->
                    <div id="m_header_topbar" class="m-topbar  m-stack m-stack--ver m-stack--general m-stack--fluid">
                        <div class="m-stack__item m-topbar__nav-wrapper">
                            <#--<ul class="m-topbar__nav m-nav m-nav--inline">
                                <li class="m-nav__item m-topbar__notifications m-topbar__notifications--img m-dropdown m-dropdown--large m-dropdown--header-bg-fill m-dropdown--arrow m-dropdown--align-center 	m-dropdown--mobile-full-width"
                                    m-dropdown-toggle="click" m-dropdown-persistent="1">
                                    <a href="/api/v1/ui/analyse/analyse?keyword=alert&channel=fb" class="m-nav__link">
                                        <span class="m-nav__link-icon">
													<i class="flaticon-music-2"></i>
												</span>
                                    </a>
                                </li>
                                <li class="m-nav__item m-topbar__user-profile m-topbar__user-profile--img  m-dropdown m-dropdown--medium m-dropdown--arrow m-dropdown--header-bg-fill m-dropdown--align-right m-dropdown--mobile-full-width m-dropdown--skin-light"
                                    m-dropdown-toggle="click">
                                    <a href="#" class="m-nav__link m-dropdown__toggle">
												<span class="m-nav__link-icon">
													<i class="flaticon-avatar"></i>
												</span>
                                        <span class="m-topbar__username m--hide">
													User
												</span>
                                    </a>
                                    <div class="m-dropdown__wrapper">
                                        <span class="m-dropdown__arrow m-dropdown__arrow--right m-dropdown__arrow--adjust"></span>
                                        <div class="m-dropdown__inner">
                                            <div class="m-dropdown__header m--align-center m--bg-brand">
                                                <div class="m-card-user m-card-user--skin-dark">
                                                    <div class="m-card-user__pic">
                                                        <i class="flaticon-user"></i>
                                                    </div>
                                                    <div class="m-card-user__details">
																<span class="m-card-user__name m--font-weight-500">
																	User Name
																</span>
                                                        <a href="" class="m-card-user__email m--font-weight-300 m-link">
                                                            user.name@gmail.com
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="m-dropdown__body">
                                                <div class="m-dropdown__content">
                                                    <ul class="m-nav m-nav--skin-light">
                                                        <li class="m-nav__section m--hide">
																	<span class="m-nav__section-text">
																		Section
																	</span>
                                                        </li>
                                                        &lt;#&ndash;<li class="m-nav__item">
                                                            <a href="/api/v1/ui/user/profile" class="m-nav__link">
                                                                <i class="m-nav__link-icon flaticon-profile-1"></i>
                                                                <span class="m-nav__link-title">
																			<span class="m-nav__link-wrap">
																				<span class="m-nav__link-text">
																					My Profile
																				</span>
																				<span class="m-nav__link-badge">
																					<span class="m-badge m-badge--success">
																						2
																					</span>
																				</span>
																			</span>
																		</span>
                                                            </a>
                                                        </li>&ndash;&gt;
                                                        <li class="m-nav__separator m-nav__separator--fit"></li>
                                                        <li class="m-nav__item">
                                                            <a href=""
                                                               class="btn m-btn--pill btn-secondary m-btn m-btn--custom m-btn--label-brand m-btn--bolder logoutBtn">
                                                                Logout
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </li>
                            </ul>-->
                        </div>
                    </div>
                    <!-- END: Topbar -->
                </div>
            </div>
        </div>
    </header>
    <!-- END: Header -->
    <!-- begin::Body -->
    <div class="m-grid__item m-grid__item--fluid m-grid m-grid--ver-desktop m-grid--desktop m-body">
        <!-- BEGIN: Left Aside -->
        <button class="m-aside-left-close  m-aside-left-close--skin-dark " id="m_aside_left_close_btn">
            <i class="la la-close"></i>
        </button>
        <div id="m_aside_left" class="m-grid__item	m-aside-left  m-aside-left--skin-dark ">
            <!-- BEGIN: Aside Menu -->
            <div
                    id="m_ver_menu"
                    class="m-aside-menu  m-aside-menu--skin-dark m-aside-menu--submenu-skin-dark m-scroller ps ps--active-y"
                    m-menu-vertical="1" m-menu-scrollable="1" m-menu-dropdown-timeout="500">
                <ul class="m-menu__nav  m-menu__nav--dropdown-submenu-arrow ">
                    <li id="menu-item-dashboard" class="m-menu__item " aria-haspopup="true">
                        <a href="/api/v1/ui/dashboard" class="m-menu__link ">
                            <i class="m-menu__link-icon la la-home"></i>
                            <span class="m-menu__link-title">
										<span class="m-menu__link-wrap">
											<span class="m-menu__link-text">
												Dashboard
											</span>
										</span>
									</span>
                        </a>
                    </li>


                    <li class="m-menu__section ">
                        <h4 class="m-menu__section-text">
                            Create
                        </h4>
                        <i class="m-menu__section-icon flaticon-more-v3"></i>
                    </li>

                    <li id="menu-item-create-script" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/create/script" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-edit"></i>
                            <span class="m-menu__link-text">Script</span>
                        </a>
                    </li>
                    <li id="menu-item-create-survey" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/create/survey" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-clipboard"></i>
                            <span class="m-menu__link-text">Survey</span>
                        </a>
                    </li>

                    <li class="m-menu__section ">
                        <h4 class="m-menu__section-text">
                            Data
                        </h4>
                        <i class="m-menu__section-icon flaticon-more-v3"></i>
                    </li>
                    <li id="menu-item-data-sheets" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/data/sheets" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-th-list"></i>
                            <span class="m-menu__link-text">Data sheets</span>
                        </a>
                    </li>
                    <li id="menu-item-data-variables" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/data/variables" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-dollar"></i>
                            <span class="m-menu__link-text">Reply variables</span>
                        </a>
                    </li>
                    <li id="menu-item-data-export" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/data/export" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-exchange"></i>
                            <span class="m-menu__link-text">Import/Export</span>
                        </a>
                    </li>

                    <li class="m-menu__section ">
                        <h4 class="m-menu__section-text">
                            Analyse
                        </h4>
                        <i class="m-menu__section-icon flaticon-more-v3"></i>
                    </li>
                    <li id="menu-item-analyse-overview" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/analyse/overview" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-eye"></i>
                            <span class="m-menu__link-text">Overview</span>
                        </a>
                    </li>
                    <#--<li id="menu-item-analyse-conversation" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/analyse/conversations" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-comments"></i>
                            <span class="m-menu__link-text">Conversations</span>
                        </a>
                    </li>
                    <li id="menu-item-analyse-analyse" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/analyse/statistics" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-area-chart"></i>
                            <span class="m-menu__link-text">Analyse</span>
                        </a>
                    </li>-->
                    <li id="menu-item-analyse-graph" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/analyse/graph" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-share-alt"></i>
                            <span class="m-menu__link-text">Graph</span>
                        </a>
                    </li>
                    <li id="menu-item-analyse-analyse" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/analyse/analyse" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-area-chart"></i>
                            <span class="m-menu__link-text">Analyse</span>
                        </a>
                    </li>



                    <li class="m-menu__section ">
                        <h4 class="m-menu__section-text">
                            Live channels
                        </h4>
                        <i class="m-menu__section-icon flaticon-more-v3"></i>
                    </li>

                    <li id="menu-item-live-fb" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/live/fb" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-facebook"></i>
                            <span class="m-menu__link-text">Facebook</span>
                        </a>
                    </li>
                    <li id="menu-item-live-chitchat" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/live/chitchat" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-comment-o"></i>
                            <span class="m-menu__link-text">Chitchat</span>
                        </a>
                    </li>

                    <li class="m-menu__section ">
                        <h4 class="m-menu__section-text">
                            System
                        </h4>
                        <i class="m-menu__section-icon flaticon-more-v3"></i>
                    </li>
                    <li id="menu-item-system-docs" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/system/docs" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-life-saver"></i>
                            <span class="m-menu__link-text">Docs</span>
                        </a>
                    </li>
                    <li id="menu-item-system-info" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/system/info" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-server"></i>
                            <span class="m-menu__link-text">Server info</span>
                        </a>
                    </li>
                    <li id="menu-item-system-apidoc" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/system/apidoc" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-code"></i>
                            <span class="m-menu__link-text">REST API</span>
                        </a>
                    </li>
                    <li id="menu-item-system-about" class="m-menu__item  m-menu__item--submenu" aria-haspopup="true" m-menu-submenu-toggle="hover">
                        <a href="/api/v1/ui/about" class="m-menu__link m-menu__toggle">
                            <i class="m-menu__link-icon la la-info-circle"></i>
                            <span class="m-menu__link-text">About</span>
                        </a>
                    </li>

                </ul>
            </div>
            <!-- END: Aside Menu -->
        </div>
        <!-- END: Left Aside -->
        <div class="m-grid__item m-grid__item--fluid m-wrapper">
          <@content.pageContent />
        </div>
    </div>
    <!-- end:: Body -->
    <!-- begin::Footer -->
    <footer class="m-grid__item		m-footer ">
        <div class="m-container m-container--fluid m-container--full-height m-page__container">
            <div class="m-stack m-stack--flex-tablet-and-mobile m-stack--ver m-stack--desktop">
                <div class="m-stack__item m-stack__item--left m-stack__item--middle m-stack__item--last">
							<span class="m-footer__copyright">
								Hackathonforgood.org - Team: Peace Informatics Lab
							</span>
                </div>
                <div class="m-stack__item m-stack__item--right m-stack__item--middle m-stack__item--first">
                    <ul class="m-footer__nav m-nav m-nav--inline m--pull-right">
                        <li class="m-nav__item">
                            <a href="/api/v1/ui/about" class="m-nav__link">
                                <span class="m-nav__link-text">About</span>
                            </a>
                        </li>
                        <#--<li class="m-nav__item">
                            <a href="/api/v1/ui/privacy" class="m-nav__link">
                                <span class="m-nav__link-text">Privacy</span>
                            </a>
                        </li>-->
                    </ul>
                </div>
            </div>
        </div>
    </footer>
    <!-- end::Footer -->
</div>
<!-- end:: Page -->

<!-- begin::Scroll Top -->
<div id="m_scroll_top" class="m-scroll-top">
    <i class="la la-arrow-up"></i>
</div>
<!-- end::Scroll Top -->


<iframe id="download_iframe" style="display:none;"></iframe>

<!--begin::Base Scripts -->
<script src="/asset5/vendors/base/vendors.bundle.js" type="text/javascript"></script>
<script src="/asset5/demo/default/base/scripts.bundle.js" type="text/javascript"></script>
<!--end::Base Scripts -->
<#--<script src="/asset5/vendors/custom/datatables/datatables.bundle.js" type="text/javascript"></script>-->

<script src="/asset5/app/js/hx.util.js" type="text/javascript"></script>
<script src="/asset5/app/js/hx.clientstate.js" type="text/javascript"></script>
<script src="/asset5/app/js/hx.ui.js" type="text/javascript"></script>
<script src="/asset5/app/js/hx.dbdoc.js" type="text/javascript"></script>

<script src="/asset5/vendors/custom/filesaver/FileSaver.min.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/plotly/plotly.min.js" type="text/javascript"></script>

<script src="/asset5/app/js/hx.plotly.js" type="text/javascript"></script>

<script src="/asset5/vendors/custom/split/split.min.js"></script>

<script src="/asset5/vendors/custom/leaflet/leaflet.js"></script>
<script src="/asset5/vendors/custom/leaflet/markercluster/leaflet.markercluster.js"></script>
<script src="/asset5/vendors/custom/leaflet/leaflet.hotline.js"></script>
<script src="/asset5/vendors/custom/c4i/Sleaflet.js"></script>

<#--<script src="/asset5/vendors/custom/js-yaml.js"></script>-->
<script src="/asset5/vendors/custom/codemirror/lib/codemirror.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/mode/simple.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/runmode/runmode.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/dialog/dialog.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/edit/matchbrackets.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/edit/closebrackets.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/search/search.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/search/searchcursor.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/search/jump-to-line.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/comment/comment.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/selection/active-line.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/mode/overlay.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/fold/foldcode.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/fold/foldgutter.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/fold/brace-fold.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/scroll/simplescrollbars.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/hint/show-hint.js"></script>
<script src="/asset5/vendors/custom/codemirror/addon/hint/anyword-hint.js"></script>
<script src="/asset5/vendors/custom/codemirror/mode/yaml-frontmatter/yaml-frontmatter.js"></script>
<script src="/asset5/vendors/custom/c4i/ccs-editor.js"></script>

<script src="/asset5/vendors/custom/showdown/showdown.min.js"></script>
<script src="/asset5/vendors/custom/showdown/showdown.accordion.js"></script>
<script src="/asset5/vendors/custom/c4i/c4i.js" type="text/javascript"></script>

<script src="/asset5/vendors/custom/cytoscape/cytoscape.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/cytoscape/cytoscape-euler.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/cytoscape/cytoscape-klay.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/cytoscape/cytoscape-cola.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/cytoscape/cytoscape-dagre.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/cytoscape/cytoscape-panzoom.js" type="text/javascript"></script>

<script>
    $(document).ready(() => {
        $('.logoutBtn').click($.hx.logout);
    })
</script>

<@content.pageScript />
</body>
<!-- end::Body -->
</html>
