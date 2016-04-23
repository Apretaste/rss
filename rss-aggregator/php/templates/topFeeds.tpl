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
<h2>Articulos Mas Nuevos, por Fuente, en la Categoria {$category->title}</h2>
<h5>Haz un click sobre el nombre de la fuente para Ver todos sus Articulos</h5>
<h5>Haz un click sobre el nombre de un Articulo para verlo completo</h5>
<table border=1 width="650 px">
	<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
		<td width="175 px"><b>Fuente</b></td>
		<td width="400 px"><b>Description</b></td>
		<td width="75 px"><b>Articulos</b></td>
	</tr>

	{foreach $feeds as $feed}
		{strip}
		{if $feed->articles}
		<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
			<td width="150 px">{link href="NOTICIAS /__{urlencode($feed->link)}" caption="{$feed->title}"}</td>
			<td width="200 px">{$feed->description}</td>
			<td width="50 px">{$feed->articleCount}</td>
		</tr>
		<tr bgcolor="{cycle values="#FFFFFF,#EEEEEE"}">
			<td colspan="3">
					{$counter=1}
					{foreach $feed->articles as $article}
						{strip}
						<table width="100%" border=1>
							<tr>
								{if $article->trimmed}
									<td width="80%">{$counter++}.&nbsp;{link href="NOTICIAS >__{urlencode($article->guid)}" caption="{$article->title}"}</td>
								{else}
									<td width="80%">{$counter++}.&nbsp;<b>{$article->title}</b></td>
								{/if}
								<td width="20%"><nobr>{date("d/m/Y H:i:s", $article->pubDate/1000)}</nobr></td>
							</tr>
							{if $article->description}
							<tr>
								<td colspan="2">{$article->description}</td>
							</tr>
							{/if}
							<tr>
								<td colspan="2">
								<table width="100%">
									<tr>
										<td align="left" width="50%">
										{if $feed->language and $feed->language!="es"}
											{link href="NOTICIAS >>_{urlencode($article->link)}" caption="Leer Articulo en su idioma original"}
										{/if}
										&nbsp;</td>
										<td align="right" width="50%">
										{if $article->trimmed}
											{link href="NOTICIAS >__{urlencode($article->link)}" caption="Leer Articulo Completo"}
										{else}
											{link href="WEB {urlencode($article->link)}" caption="Leer Pagina de la Noticia"}
										{/if}
										&nbsp;</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						<hr>
						{/strip}
					{/foreach}
			</td>
		</tr>
		{/if}
		{/strip}
	{/foreach}
</table>
</center>

{space15}

{space30}

<!--  https://tech.yandex.com/translate/doc/dg/concepts/design-requirements-docpage/ -->
Traducción en tiempo real por medio de Yandex. <a href="http://translate.yandex.com/">Powered by Yandex.Translate</a>

