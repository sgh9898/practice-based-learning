package com.sgh.demo.common.temp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * [实体类] 学校信息
 *
 * @author Song gh
 * @version 2024/04/29
 */
@Entity
@Getter
@Setter
@Table(name = "xxxx")
@ApiModel("[实体类] 学校信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xxxx {

    /** 学校信息id */
    @JsonAlias("xxxx_id")
    @ApiModelProperty("学校信息id")
    private String xxxxId;

    /** 机构名称 */
    @JsonAlias("jgmc")
    @ApiModelProperty("机构名称")
    private String jgmc;

    /** 机构编码 */
    @Id
    @JsonAlias("jgbm")
    @ApiModelProperty("机构编码")
    private String jgbm;

    /** 办学类型 */
    @JsonAlias("bxlx")
    @ApiModelProperty("办学类型")
    private String bxlx;

    /** 办学类型编码 */
    @JsonAlias("bxlxbm")
    @ApiModelProperty("办学类型编码")
    private String bxlxbm;

    /** 举办者性质 */
    @JsonAlias("jbzxz")
    @ApiModelProperty("举办者性质")
    private String jbzxz;

    /** 举办者类型分组 */
    @JsonAlias("jbzlxfz")
    @ApiModelProperty("举办者类型分组")
    private String jbzlxfz;

    /** 举办者类型 */
    @JsonAlias("jbzlx")
    @ApiModelProperty("举办者类型")
    private String jbzlx;

    /** 举办者类型编码 */
    @JsonAlias("jbzlxbm")
    @ApiModelProperty("举办者类型编码")
    private String jbzlxbm;

    /** 城乡分组 */
    @JsonAlias("cxfz")
    @ApiModelProperty("城乡分组")
    private String cxfz;

    /** 城乡类型 */
    @JsonAlias("cxlx")
    @ApiModelProperty("城乡类型")
    private String cxlx;

    /** 城乡类型编码 */
    @JsonAlias("cxlxbm")
    @ApiModelProperty("城乡类型编码")
    private String cxlxbm;

    /** 是否本年新增 */
    @JsonAlias("sfbnxz")
    @ApiModelProperty("是否本年新增")
    private String sfbnxz;

    /** 是否民族 */
    @JsonAlias("sfmz")
    @ApiModelProperty("是否民族")
    private String sfmz;

    /** 是否具有双语教学班 */
    @JsonAlias("sfjysyjxb")
    @ApiModelProperty("是否具有双语教学班")
    private String sfjysyjxb;

    /** 是否普惠性民办幼儿园 */
    @JsonAlias("sfphxmbyey")
    @ApiModelProperty("是否普惠性民办幼儿园")
    private String sfphxmbyey;

    /** 法人和其他组织统一社会信用代码 */
    @JsonAlias("frhqtzztyshxydm")
    @ApiModelProperty("法人和其他组织统一社会信用代码")
    private String frhqtzztyshxydm;

    /** 是否乡镇中心幼儿园 */
    @JsonAlias("sfxzzxyey")
    @ApiModelProperty("是否乡镇中心幼儿园")
    private String sfxzzxyey;

    /** 是否附属学校(园) */
    @JsonAlias("sffsxxy")
    @ApiModelProperty("是否附属学校(园)")
    private String sffsxxy;

    /** 附属于高校机构名称 */
    @JsonAlias("fsygxjgmc")
    @ApiModelProperty("附属于高校机构名称")
    private String fsygxjgmc;

    /** 是否营利性 */
    @JsonAlias("sfylx")
    @ApiModelProperty("是否营利性")
    private String sfylx;

    /** 营利性民办学校(机构)简称 */
    @JsonAlias("ylxmbxxjgjc")
    @ApiModelProperty("营利性民办学校(机构)简称")
    private String ylxmbxxjgjc;

    /** 所在地编码 */
    @JsonAlias("szdbm")
    @ApiModelProperty("所在地编码")
    private String szdbm;

    /** 所在地区划一级 */
    @JsonAlias("szdqhyj")
    @ApiModelProperty("所在地区划一级")
    private String szdqhyj;

    /** 所在地区划二级 */
    @JsonAlias("szdqhej")
    @ApiModelProperty("所在地区划二级")
    private String szdqhej;

    /** 所在地区划三级 */
    @JsonAlias("szdqhsanj")
    @ApiModelProperty("所在地区划三级")
    private String szdqhsanj;

    /** 所在地区划四级 */
    @JsonAlias("szdqhsij")
    @ApiModelProperty("所在地区划四级")
    private String szdqhsij;

    /** 所在地区划五级 */
    @JsonAlias("szdqhwj")
    @ApiModelProperty("所在地区划五级")
    private String szdqhwj;

    /** 统计机构编码 */
    @JsonAlias("tjjgbm")
    @ApiModelProperty("统计机构编码")
    private String tjjgbm;

    /** 统计一级 */
    @JsonAlias("tjyj")
    @ApiModelProperty("统计一级")
    private String tjyj;

    /** 统计二级 */
    @JsonAlias("tjej")
    @ApiModelProperty("统计二级")
    private String tjej;

    /** 统计三级 */
    @JsonAlias("tjsj")
    @ApiModelProperty("统计三级")
    private String tjsj;

    /** 采集机构编码 */
    @JsonAlias("cjjgbm")
    @ApiModelProperty("采集机构编码")
    private String cjjgbm;

    /** 采集一级 */
    @JsonAlias("cjyj")
    @ApiModelProperty("采集一级")
    private String cjyj;

    /** 采集二级 */
    @JsonAlias("cjej")
    @ApiModelProperty("采集二级")
    private String cjej;

    /** 采集三级 */
    @JsonAlias("cjsanj")
    @ApiModelProperty("采集三级")
    private String cjsanj;

    /** 采集四级 */
    @JsonAlias("cjsij")
    @ApiModelProperty("采集四级")
    private String cjsij;

    /** 经度 */
    @JsonAlias("jd")
    @ApiModelProperty("经度")
    private String jd;

    /** 纬度 */
    @JsonAlias("wd")
    @ApiModelProperty("纬度")
    private String wd;

    /** 校长名称 */
    @JsonAlias("xcmc")
    @ApiModelProperty("校长名称")
    private String xcmc;

    /** 统计负责人名称 */
    @JsonAlias("tjfzrmc")
    @ApiModelProperty("统计负责人名称")
    private String tjfzrmc;

    /** 统计负责人部门 */
    @JsonAlias("tjfzrbm")
    @ApiModelProperty("统计负责人部门")
    private String tjfzrbm;

    /** 统计负责人职务 */
    @JsonAlias("tjfzrzw")
    @ApiModelProperty("统计负责人职务")
    private String tjfzrzw;

    /** 填表人名称 */
    @JsonAlias("tbrmc")
    @ApiModelProperty("填表人名称")
    private String tbrmc;

    /** 填表人部门 */
    @JsonAlias("tbrbm")
    @ApiModelProperty("填表人部门")
    private String tbrbm;

    /** 填表人职务 */
    @JsonAlias("tbrzw")
    @ApiModelProperty("填表人职务")
    private String tbrzw;

    /** 填表人联系电话 */
    @JsonAlias("tbrlxdh")
    @ApiModelProperty("填表人联系电话")
    private String tbrlxdh;

    /** 邮编 */
    @JsonAlias("yb")
    @ApiModelProperty("邮编")
    private String yb;

    /** 电话区号 */
    @JsonAlias("dhqh")
    @ApiModelProperty("电话区号")
    private String dhqh;

    /** 办公电话 */
    @JsonAlias("bgdh")
    @ApiModelProperty("办公电话")
    private String bgdh;

    /** 移动电话 */
    @JsonAlias("yddh")
    @ApiModelProperty("移动电话")
    private String yddh;

    /** 网址 */
    @JsonAlias("wz")
    @ApiModelProperty("网址")
    private String wz;

    /** 电子邮箱 */
    @JsonAlias("dzyx")
    @ApiModelProperty("电子邮箱")
    private String dzyx;

    /** 是否通电 */
    @JsonAlias("sftd")
    @ApiModelProperty("是否通电")
    private String sftd;

    /** 接入互联网 */
    @JsonAlias("jrhlw")
    @ApiModelProperty("接入互联网")
    private String jrhlw;

    /** 无线网全覆盖 */
    @JsonAlias("wxwqfg")
    @ApiModelProperty("无线网全覆盖")
    private String wxwqfg;

    /** 学校供水方式 */
    @JsonAlias("xxgsfs")
    @ApiModelProperty("学校供水方式")
    private String xxgsfs;

    /** 学校厕所情况 */
    @JsonAlias("xxcsqk")
    @ApiModelProperty("学校厕所情况")
    private String xxcsqk;

    /** 洗手设施 */
    @JsonAlias("xsss")
    @ApiModelProperty("洗手设施")
    private String xsss;

    /** 建立家长委员会 */
    @JsonAlias("jljcwyh")
    @ApiModelProperty("建立家长委员会")
    private String jljcwyh;

    /** 安全保卫人员 */
    @JsonAlias("aqbwry")
    @ApiModelProperty("安全保卫人员")
    private String aqbwry;

    /** 是否乡镇中心小学 */
    @JsonAlias("sfxzzxxx")
    @ApiModelProperty("是否乡镇中心小学")
    private String sfxzzxxx;

    /** 是否具有附设班 */
    @JsonAlias("sfjyfsb")
    @ApiModelProperty("是否具有附设班")
    private String sfjyfsb;

    /** 体育运动场(馆)面积是否达标 */
    @JsonAlias("tyydcgmjsfdb")
    @ApiModelProperty("体育运动场(馆)面积是否达标")
    private String tyydcgmjsfdb;

    /** 体育器械配备是否达标 */
    @JsonAlias("tyqxpbsfdb")
    @ApiModelProperty("体育器械配备是否达标")
    private String tyqxpbsfdb;

    /** 音乐器材配备是否达标 */
    @JsonAlias("ylqcpbsfdb")
    @ApiModelProperty("音乐器材配备是否达标")
    private String ylqcpbsfdb;

    /** 美术器材配备是否达标 */
    @JsonAlias("msqcpbsfdb")
    @ApiModelProperty("美术器材配备是否达标")
    private String msqcpbsfdb;

    /** 数学自然实验仪器是否达标(小学) */
    @JsonAlias("syyqsfdb")
    @ApiModelProperty("数学自然实验仪器是否达标(小学)")
    private String syyqsfdb;

    /** 学校首席信息官(CIO) */
    @JsonAlias("xxsxxxgcio")
    @ApiModelProperty("学校首席信息官(CIO)")
    private String xxsxxxgcio;

    /** 校医院(卫生/保健室) */
    @JsonAlias("xyywsbjs")
    @ApiModelProperty("校医院(卫生/保健室)")
    private String xyywsbjs;

    /** 县级以上骨干教师(小学) */
    @JsonAlias("xjysggjs")
    @ApiModelProperty("县级以上骨干教师(小学)")
    private String xjysggjs;

    /** 专职校医 */
    @JsonAlias("zzxy")
    @ApiModelProperty("专职校医")
    private String zzxy;

    /** 专职保健人员 */
    @JsonAlias("zzbjry")
    @ApiModelProperty("专职保健人员")
    private String zzbjry;

    /** 与外方缔结"友好学校"数量 */
    @JsonAlias("ywfdjyhxxsl")
    @ApiModelProperty("与外方缔结\"友好学校\"数量")
    private String ywfdjyhxxsl;

    /** 政府购买学位数 */
    @JsonAlias("zfgmxws")
    @ApiModelProperty("政府购买学位数")
    private String zfgmxws;

    /** 其中:用于进城务工人员随迁子女的学位数 */
    @JsonAlias("qzyyjcwgrysqzndxws")
    @ApiModelProperty("其中:用于进城务工人员随迁子女的学位数")
    private String qzyyjcwgrysqzndxws;

    /** 预防艾滋病教育和性教育相关课程和活动 */
    @JsonAlias("yfazbjyhxjyxgkchhd")
    @ApiModelProperty("预防艾滋病教育和性教育相关课程和活动")
    private String yfazbjyhxjyxgkchhd;

    /** 校外实习实训场所 */
    @JsonAlias("xwsxsxcs")
    @ApiModelProperty("校外实习实训场所")
    private String xwsxsxcs;

    /** 专业数 */
    @JsonAlias("zys")
    @ApiModelProperty("专业数")
    private String zys;

    /** 高校类型 */
    @JsonAlias("gxlx")
    @ApiModelProperty("高校类型")
    private String gxlx;

    /** 国家高水平高职专业数量 */
    @JsonAlias("gjgspgzzysl")
    @ApiModelProperty("国家高水平高职专业数量")
    private String gjgspgzzysl;

    /** 省级高水平高职专业数量 */
    @JsonAlias("sjgspgzzysl")
    @ApiModelProperty("省级高水平高职专业数量")
    private String sjgspgzzysl;

    /** 硕士学位授权一级学科点 */
    @JsonAlias("ssxwsqyjxkd")
    @ApiModelProperty("硕士学位授权一级学科点")
    private String ssxwsqyjxkd;

    /** 硕士学位授权二级学科点（不含一级学科覆盖点） */
    @JsonAlias("ssxwsqejxkdbhyjxkfgd")
    @ApiModelProperty("硕士学位授权二级学科点（不含一级学科覆盖点）")
    private String ssxwsqejxkdbhyjxkfgd;

    /** 博士学位授权一级学科点 */
    @JsonAlias("bsxwsqyjxkd")
    @ApiModelProperty("博士学位授权一级学科点")
    private String bsxwsqyjxkd;

    /** 博士学位授权二级学科点（不含一级学科覆盖点） */
    @JsonAlias("bsxwsqejxkdbhyjxkfgd")
    @ApiModelProperty("博士学位授权二级学科点（不含一级学科覆盖点）")
    private String bsxwsqejxkdbhyjxkfgd;

    /** 国家一流学科数量 */
    @JsonAlias("gjylxksl")
    @ApiModelProperty("国家一流学科数量")
    private String gjylxksl;

    /** 省级一流学科数量 */
    @JsonAlias("sjylxksl")
    @ApiModelProperty("省级一流学科数量")
    private String sjylxksl;

    /** 博士后科研流动站 */
    @JsonAlias("bshkyldz")
    @ApiModelProperty("博士后科研流动站")
    private String bshkyldz;

    /** 总专业数 */
    @JsonAlias("zzys")
    @ApiModelProperty("总专业数")
    private String zzys;

    /** 普通本科专业数 */
    @JsonAlias("ptbkzys")
    @ApiModelProperty("普通本科专业数")
    private String ptbkzys;

    /** 高等职业教育本科专业数 */
    @JsonAlias("gdzyjybkzys")
    @ApiModelProperty("高等职业教育本科专业数")
    private String gdzyjybkzys;

    /** 高等职业教育专科专业数 */
    @JsonAlias("gdzyjyzkzys")
    @ApiModelProperty("高等职业教育专科专业数")
    private String gdzyjyzkzys;

    /** 授予同等学力申请硕士学位人数 */
    @JsonAlias("sytdxlsqssxwrs")
    @ApiModelProperty("授予同等学力申请硕士学位人数")
    private String sytdxlsqssxwrs;

    /** 授予同等学力申请博士学位人数 */
    @JsonAlias("sytdxlsqbsxwrs")
    @ApiModelProperty("授予同等学力申请博士学位人数")
    private String sytdxlsqbsxwrs;

    /** 中国科学院院士(人事关系在本校) */
    @JsonAlias("zgkxyysrsgxzbx")
    @ApiModelProperty("中国科学院院士(人事关系在本校)")
    private String zgkxyysrsgxzbx;

    /** 中国工程院院士(人事关系在本校) */
    @JsonAlias("zggcyysrsgxzbx")
    @ApiModelProperty("中国工程院院士(人事关系在本校)")
    private String zggcyysrsgxzbx;

    /** 博物馆 */
    @JsonAlias("bwg")
    @ApiModelProperty("博物馆")
    private String bwg;

    /** 美术馆 */
    @JsonAlias("msg")
    @ApiModelProperty("美术馆")
    private String msg;

    /** 音乐厅和剧场 */
    @JsonAlias("ylthjc")
    @ApiModelProperty("音乐厅和剧场")
    private String ylthjc;

    /** 学校附属医院 */
    @JsonAlias("xxfsyy")
    @ApiModelProperty("学校附属医院")
    private String xxfsyy;

    /** 学校附属幼儿园、中小学 */
    @JsonAlias("xxfsyeyzxx")
    @ApiModelProperty("学校附属幼儿园、中小学")
    private String xxfsyeyzxx;
}
