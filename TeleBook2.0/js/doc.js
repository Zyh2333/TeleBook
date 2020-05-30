     

$(
		function save(){
			var telphone = document.getElementById("telphone").value;
			var name = document.getElementById("name").value;
			var table=document.getElementById("all_tele");
			var len=table.rows.length;
			var row=table.insertRow(len);
			row.insertCell(0).innerHTML=name;
			row.insertCell(1).innerHTML=value;
			row.insertCell(2).innerHTML="<a href=# οnclick='dele(this.parentNode.parentNode)'>删除</a>&nbsp;"
	                                    +"<a href=# οnclick='update(this.parentNode.parentNode)'>修改</a>";
			alert("添加成功！");
		}
		function dele(currentRow){
			var table = document.getElementById("table");
				if(confirm("确定要删除吗？")){
					table.deleteRow(currentRow.rowIndex);
				}
		}
		function update(currentRow){
			var table = document.getElementById("table");
			var cells=currentRow.cells;
				
				for(var i=1;i<cells.length;i++){
					if(i==cells.length-1){
						cells[i].innerHTML=
						"<a href=# οnclick='dele(this.parentNode.parentNode)'>删除</a>&nbsp;"
						+"<a href=# οnclick='saveItem(this.parentNode.parentNode)'>保存</a>";
					}
					else{
						var oldValue=cells[i].innerText;
						cells[i].innerHTML="<input type=text  value="+oldValue+" />";
					}	
				}
		}
		
		function saveItem(currentRow){
			var table = document.getElementById("table");
			var cells=currentRow.cells;
				
				for(var i=1;i<cells.length;i++){
					if(i==cells.length-1){
						cells[i].innerHTML=
						"<a href=# οnclick='dele(this.parentNode.parentNode)'>删除</a>&nbsp;"
						+"<a href=# οnclick='update(this.parentNode.parentNode)'>修改</a>";
					}
					else{
						var value=cells[i].firstChild.value;
						cells[i].innerHTML=value;
					}	
				}
		}
	
		function find(){
			var search_name = document.getElementById("search_name").value;
			var table=document.getElementById("all_tele");
			var len=table.rows.length;
			var phonenumber;
			for(var i=1;i<len;i++){
				if(table.rows[i].cells[0].innerHTML==search_name){
					phonenumber=table.rows[i].cells[1].innerHTML;
					break;
				}
			}
			var find_tel = document.getElementById("result");
			find_tel.innerHTML = search_name +"电话号码是：" +phonenumber;
		}
		
		function reset(){
			var telphone = document.getElementById("telphone");
			var name = document.getElementById("name"); 
		}
		
);
		
