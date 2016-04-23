<h1>Bienvenido al servicio de Noticias de Apretaste!</h1>
<p>Estamos añadiendo mas fuentes de noticias todos los días, abriendo Cuba al mundo.</p>

{space10}

<center>
<h2>Categorias para Noticias</h2>
<table border=1 width="400 px">
	<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
		<td width="150 px"><b>Categoria</b></td>
		<td width="200 px"><b>Descripción</b></td>
		<td width="50 px"><b>Articulos</b></td>
	</tr>

	{foreach $categories as $category}
		{strip}
		<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
			<td width="150 px">{link href="NOTICIAS {$category->title}" caption="{$category->title}"}</td>
			<td width="200 px">{$category->description}</td>
			<td width="50 px">{$category->articleCount}</td>
		</tr>
		{/strip}
	{/foreach}
</table>
</center>

{space10}

<center>
<table border=1 width="650 px">
	<tr>
		<td width="80%"><b>{$article->title}</b></td>
		<td width="20%"><nobr>{date("d/m/Y H:i:s", $article->pubDate/1000)}</nobr></td>
	</tr>
	<tr>
		<td colspan="2">{$article->description}</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			{link href="WEB {urlencode($article->link)}" caption="Leer Pagina de la Noticia"}
		</td>
	</tr>
</table>
</center>

{space15}

{space30}

<!--  https://tech.yandex.com/translate/doc/dg/concepts/design-requirements-docpage/ -->
Traducción en tiempo real por medio de Yandex. <a href="http://translate.yandex.com/">Powered by Yandex.Translate</a>

