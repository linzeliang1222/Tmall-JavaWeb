<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>

<script>
    $(function () {
        $(".createOrderForm").submit(function () {
            if ($("#myAddress").val().length == 0) {
                $(".addressError").show();
                return false;
            }

            if ($(".receiver").val().length == 0) {
                $(".receiverError").show();
                return false;
            }

            if ($(".mobile").val().length == 0) {
                $(".mobileError").html("请输入手机号码");
                $(".mobileError").show();
                return false;
            } else if ($(".mobile").val().length != 11) {
                $(".mobileError").html("手机号码有误");
                $(".mobileError").show();
                return false;
            }

            return true;
        });

        $("#myAddress").blur(function () {
            var address = $(this).val();
            if (address.length == 0) {
                $(".addressError").show();
            } else {
                $(".addressError").hide();
            }
        });

        $(".receiver").blur(function () {
            var receiver = $(this).val();
            if (receiver.length == 0) {
                $(".receiverError").show();
            } else {
                $(".receiverError").hide();
            }
        });

        $(".mobile").blur(function () {
            var mobile = $(this).val();
            if (mobile.length == 0) {
                $(".mobileError").html("请输入手机号码");
                $(".mobileError").show();
            } else if (mobile.length != 11) {
                $(".mobileError").html("手机号码有误");
                $(".mobileError").show();
            } else {
                $(".mobileError").hide();
            }
        });
    });
</script>

<div class="buyPageDiv">
    <form action="forecreateOrder" method="post" class="createOrderForm">
        <div class="buyFlow">
            <img class="pull-left" src="img/site/simpleLogo.png">
            <img class="pull-right" src="img/site/buyflow.png">
            <div style="clear:both"></div>
        </div>
        <div class="address">
            <div class="addressTip">输入收货地址</div>
            <div>
                <table class="addressTable">
                    <tr>
                        <td class="firstColumn">
                            详细地址<span class="redStar">*</span>
                            <br><span class="addressError" style="display: none; color: red;">请输入收货地址</span>
                        </td>
                        <td>
                            <textarea id="myAddress" rows="5" cols="30" name="address" placeholder="建议您如实填写详细收货地址，例如接到名称，门牌好吗，楼层和房间号等信息" style="resize: none;"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>邮政编码</td>
                        <td>
                            <input  name="post" placeholder="默认为000000" type="text">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            收货人姓名<span class="redStar">*</span>
                            <br><span class="receiverError" style="display: none; color: red;">请输入姓名</span>
                        </td>
                        <td>
                            <input  name="receiver" placeholder="长度不超过25个字符" type="text" class="receiver">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            手机号码 <span class="redStar">*</span>
                            <br><span class="mobileError" style="display: none; color: red;"></span>
                        </td>
                        <td>
                            <input name="mobile" placeholder="请输入11位手机号码" type="text" class="mobile">
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="productList">
            <div class="productListTip">确认订单信息</div>

            <table class="productListTable">
                <thead>
                <tr>
                    <th colspan="2" class="productListTableFirstColumn">
                        <img class="tmallbuy" src="img/site/tmallbuy.png">
                        <a class="marketLink" href="#nowhere">店铺：天猫店铺</a>
                        <a class="wangwanglink" href="#nowhere"> <span class="wangwangGif"></span> </a>
                    </th>
                    <th>单价</th>
                    <th>数量</th>
                    <th>小计</th>
                    <th>配送方式</th>
                </tr>
                <tr class="rowborder">
                    <td  colspan="2" ></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                </thead>
                <tbody class="productListTableTbody">
                <c:forEach items="${ois}" var="oi" varStatus="st" >
                    <tr class="orderItemTR">
                        <td class="orderItemFirstTD"><img class="orderItemImg" src="img/productSingle_middle/${oi.product.firstProductImage.id}.jpg"></td>
                        <td class="orderItemProductInfo">
                            <a  href="foreproduct?pid=${oi.product.id}" class="orderItemProductLink">
                                    ${oi.product.name}
                            </a>
                            <img src="img/site/creditcard.png" title="支持信用卡支付">
                            <img src="img/site/7day.png" title="消费者保障服务,承诺7天退货">
                            <img src="img/site/promise.png" title="消费者保障服务,承诺如实描述">
                        </td>
                        <td>
                            <span class="orderItemProductPrice">￥<fmt:formatNumber type="number" value="${oi.product.promotePrice}" minFractionDigits="2"/></span>
                        </td>
                        <td>
                            <span class="orderItemProductNumber">${oi.number}</span>
                        </td>
                        <td><span class="orderItemUnitSum">
						￥<fmt:formatNumber type="number" value="${oi.number*oi.product.promotePrice}" minFractionDigits="2"/>
						</span></td>
                        <c:if test="${st.count==1}">
                            <td rowspan="5"  class="orderItemLastTD">
                                <label class="orderItemDeliveryLabel">
                                    <input type="radio" value="" checked="checked">
                                    普通配送
                                </label>

                                <select class="orderItemDeliverySelect" class="form-control">
                                    <option>快递 免邮费</option>
                                </select>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>

                </tbody>

            </table>
            <div class="orderItemSumDiv">
                <div class="pull-left">
                    <span class="leaveMessageText">给卖家留言:</span>
                    <span>
					    <img class="leaveMessageImg" src="img/site/leaveMessage.png">
				    </span>
                    <span class="leaveMessageTextareaSpan">
					    <textarea name="userMessage" class="leaveMessageTextarea"></textarea>
					    <div>
						    <span>还可以输入200个字符</span>
					    </div>
				    </span>
                </div>
                <span class="pull-right">店铺合计(含运费): ￥<fmt:formatNumber type="number" value="${total}" minFractionDigits="2"/></span>
            </div>

        </div>

        <div class="orderItemTotalSumDiv">
            <div class="pull-right">
                <span>实付款：</span>
                <span class="orderItemTotalSumSpan">￥<fmt:formatNumber type="number" value="${total}" minFractionDigits="2"/></span>
            </div>
        </div>

        <div class="submitOrderDiv">
            <button type="submit" class="submitOrderButton">提交订单</button>
        </div>
    </form>
</div>