MapsApp.getDegree=function(pPoint,dMeter){
	var dDegree=1;
	var pPoint1=new Point(pPoint.x+dDegree,pPoint.y);
	var dMeter1=GetDistanceInLL(pPoint,pPoint1);
	var dResult=dDegree*dMeter/dMeter1;
	return dResult;
};
_C_P=0.0174532925199432957692222222222;
function GetDistanceInLL(p1, p2)
{
   var d=new Number(0);
   if(_MapSpanScale ==1){
      var dlon = (p2.x - p1.x)*_C_P;
      //弧度
      var dlat = (p2.y - p1.y)*_C_P;
      var a = Math.sin(0.5*dlat)*Math.sin(0.5*dlat)+Math.cos(p1.y*_C_P)*Math.cos(p2.y*_C_P)*(Math.sin(0.5*dlon)*Math.sin(0.5*dlon));
      a=Math.abs(a);
		
      if(a>1){
         alert("不合法数据:"+"a:"+a+",P1:"+p1.toString()+",P2:"+p2.toString());
      }
		
      var c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
      d = c*6371008.77141506;
      
      //此方法在当地图视角大于180跨度时计算有问题即此方法计算的是地球表面两点之间的最短距离，两点没有先后顺序 20100411
      if(Math.abs(p2.x-p1.x)>180 || Math.abs(p2.y-p1.y)>180)d = 2*Math.PI*6371008.77141506 - d;
		
   }
   else{
      var p2Len=(p2.x - p1.x)*(p2.x - p1.x)+(p2.y - p1.y)*(p2.y - p1.y);
      d=Math.sqrt(p2Len);
   }
    
   d=Math.ceil(d);
    
   return d;
   //单位是米
}